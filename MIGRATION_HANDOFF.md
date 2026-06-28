# Twittur — Compose Multiplatform Migration: State & Handoff

> Working state for the KMP/CMP migration. Read this top-to-bottom before continuing.
> Branch: `feature/compose-multiplatform`. Last updated: 2026-06-29.

---

## 0. TL;DR — where things stand

- This repo is mid-migration from a single-module **Android XML/Fragment app** to **Compose Multiplatform** (Android, iOS, Desktop, later Web).
- There were **two tracks**; the user has now committed to **Track A** ("make it multiplatform").
- **Track B (done, never compiled):** all **39/39** legacy fragments in `:app` were converted to `ComposeView`-hosted **Android-only** Compose screens. These are the **visual/behavioral SPEC** for Track A. `:app` is excluded from the CMP Gradle build, so this code was never compiled.
- **Track A (active):** big-bang CMP rewrite into `:composeApp` + `:core:*` + `:feature:*`. **Slices COMPLETE and VERIFIED on Android + Desktop/JVM + iOS: `search` (pilot), `notification`, `auth`, `home` hub, `profile` (view/edit/delete), and `tweet` (feed/compose/delete **+ detail/replies/post-reply/list-of-likes**; 2026-06-29).** The Search slice is the template; the rest prove the recipe replicates. **iOS is now part of the verified build** (framework compiles + links) — see Section 2.
- **App-level routing is now wired (2026-06-28):** the app is **auth-first** — `App()` reads `SessionSource.isRemembered()` and sets `startDestination = HomeRoute` (auto-login) or `SignInRoute`. Flow: SignIn → StayIn → Home; Home → Feed/Search/Notifications/Profile/Logout. See Section 8a.
- **NETWORK-REQUEST FIX (2026-06-29):** registration + login were reported broken. Root cause: **registration reused the *request* DTO as the *response* type** (`post<StudentRegistrationDto, StudentRegistrationDto>` / professor), but the server's success body OMITS the required `password` field → kotlinx.serialization `MissingFieldException` → mapped to `SERIALIZATION` error **even on a successful 2xx** (account was actually created). Login then *appeared* broken because no valid account existed. **Login's own data path was always correct** (uses the all-nullable `AuthUserDto`). The identical bug existed in **profile edit** (`put<EditProfileDto, EditProfileDto>`). **Fix:** for these fire-and-forget writes the response type is now **`Unit`** — Ktor's `DefaultTransform` special-cases `Unit::class` at the `Parse` phase and *discards* the body before ContentNegotiation runs (verified in ktor-client-core 3.5.0 sources; same mechanism `delete<Unit>` already relied on). See Section 7 gotcha #13. Note: the backend also validates `major` (enum `SE/BM/IT/ME/CIE/AD/AE`) and `studentId` (opaque server format) — those are user-input concerns, not code bugs.
- **Next task:** keep replicating the slice pattern feature-by-feature (Section 8 is the recipe). Remaining: **follow** (followers/following — `FollowViewModel`; needed to enable detail's deferred Follow button), chat/settings/feedback/connection (Section 10), plus the deferred **profile image picker** (expect/actual) and the deferred **tweet like/edit/report** + **detail more-settings (delete/report)**. Home shows a **Feed** entry now; folding the feed into Home as its literal body (drawer/FAB) is still open. **`detail` was implemented *inside* `:feature:tweet`** (not a standalone `:feature:detail`) to honor the locked "features never depend on each other" rule — detail is the tweet's own detail view, so it reuses `Tweet`/`TweetItem` in-module; if a separate module is ever wanted, lift `Tweet`+`TweetItem` to `core` first.

---

## 1. The two tracks (why there's "duplicate" UI)

| | Track A (ACTIVE) | Track B (reference only) |
|---|---|---|
| What | Compose **Multiplatform** rewrite | Android-only Compose conversion of legacy `:app` |
| Where | `:composeApp`, `:core:*`, `:feature:*` (commonMain) | `app/src/main/java/.../presentation/screens/*` + converted fragments |
| Stack | Koin, Ktor, Coil3, Compose-Nav, CMP resources, StateFlow | Hilt, Retrofit, Coil2, Glide, Airbnb Lottie, LiveData, Fragments, `R.*` |
| Toolchain | Kotlin 2.4.0 | Kotlin 1.9.21 / Compose-compiler 1.5.7 |
| In Gradle build? | **Yes** (settings.gradle.kts) | **No** — `:app` intentionally excluded |
| Status | Search slice verified; rest pending | 39/39 fragments converted, **uncommitted, never compiled** |

The two **cannot share one Gradle build** (Kotlin 1.9 vs 2.4). Track B screens are Android-only and **not** reusable in `:composeApp` — they are the **spec** you port from, swapping each Android dep for its KMP equivalent.

---

## 2. Toolchain (LOCKED — do not change)

- Gradle **9.1.0**, AGP **9.0.1**, Kotlin **2.4.0**, Compose Multiplatform **1.11.1**, compileSdk **36** / minSdk **26**.
- **ALWAYS build with the Android Studio JBR 21**, never system Java:
  ```bash
  export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  ```
- AGP 9 uses the **new KMP DSL** (`com.android.kotlin.multiplatform.library` + `kotlin { androidLibrary { } }`, no build variants).
- **No `:build-logic`** / convention plugins exist — every module has an **inline** `build.gradle.kts` (copy an existing one).
- The version catalog `gradle/libs.versions.toml` already has **all** Track A deps staged: Ktor 3.5, Koin 4.2, **Coil3** 3.5, navigation-compose 2.9, `components-resources`, lifecycle-viewmodel-compose, `kotlinSerialization` plugin. **No Lottie.**

### Build / verify commands (what's been run, all green — run all three every time)
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :androidApp:assembleDebug --console=plain                          # Android → androidApp-debug.apk
./gradlew :composeApp:compileKotlinJvm --console=plain                       # Desktop/JVM
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 --console=plain    # iOS — compiles + LINKS ComposeApp.framework
```
Gotcha: if you pipe gradle through `tee`, end the script with `exit ${PIPESTATUS[0]}` — a trailing `echo` masks the real exit code (a `BUILD FAILED` will otherwise look like exit 0).

**iOS IS NOW VERIFIED** (2026-06-28): `linkDebugFrameworkIosSimulatorArm64` = BUILD SUCCESSFUL → the `ComposeApp.framework` Xcode consumes links cleanly. **commonMain is analyzed against the iOS leaf, so an Android+JVM-green build can still be iOS-RED — always run the iOS link too.** The bundle-id warning is silenced via `binaryOption("bundleId", "com.example.twitturin.composeApp")` in the framework block.

---

## 3. Module layout (settings.gradle.kts)

```
:composeApp                         ← shared root App() + NavHost + Koin startup; exports iOS framework "ComposeApp"
:androidApp  :desktopApp            ← thin platform entrypoints (iosApp = Xcode project; :webApp deferred, NOT included)
:core:domain :core:data :core:presentation :core:design-system
:feature:search:domain :feature:search:data :feature:search:presentation         ← the PILOT
:feature:notification:domain :feature:notification:data :feature:notification:presentation   ← 2nd feature (DONE + verified)
:feature:auth:domain :feature:auth:data :feature:auth:presentation                           ← 3rd feature (DONE + verified, incl. iOS)
:feature:home:presentation                                                                   ← post-login hub (presentation-only; thin until tweet/feed lands)
:feature:profile:domain :feature:profile:data :feature:profile:presentation                  ← profile view/edit/delete (image picker deferred)
:feature:tweet:domain :feature:tweet:data :feature:tweet:presentation                         ← tweet feed/compose/delete + detail/replies/likes-list (like/edit/report deferred)
```
`:app` (legacy) is **not** in settings.gradle.kts. House structure follows the `android-*` skills: feature-layered, `presentation → domain ← data`, features never depend on each other, shared code goes to `core:*`.

---

## 4. What `:core:*` already provides (the foundation you build on)

- **core:domain** — `Result<D, E: Error>` (`Success`/`Error` + `map`/`onSuccess`/`onFailure`/`asEmptyResult`, `EmptyResult<E>` = `Result<Unit,E>`), `DataError` (`Network`/`Local` enums), `SessionSource` (now also `isRemembered()`/`setRemembered()` for stay-logged-in, added for `auth`).
- **core:data** — `HttpClientFactory.create(engine, sessionSource)` (Ktor, Auth bearer, JSON); `SafeCall.kt` with `HttpClient.get/post/delete(route, queryParameters)` → `Result<T, DataError.Network>`; **base URL** `https://twitturin-api.onrender.com/api/` baked into `constructRoute()`. **NOTE (2026-06-28):** `constructRoute()` now **passes absolute `http(s)://` URLs through unchanged** (added for the GitHub releases endpoint used by `notification`) — so a repo can call any host, not just the twitturin base. Bearer auth is NOT sent pre-emptively (no `sendWithoutRequest`), so calling public 3rd-party hosts like GitHub won't leak the session token. `httpClientEngine()` expect/actual (okhttp android+jvm, darwin ios); **`coreDataModule`** (Koin: `Settings`, `SessionSource`, `HttpClient`). `SafeCall` now has **`get`/`post`/`put`/`delete`** (`put` added for profile edit). The bearer plugin now sends the token **preemptively to the twitturin host only** (`sendWithoutRequest { it.url.host == "twitturin-api.onrender.com" }`) so authenticated PUT/DELETE/POST don't need a 401-challenge round-trip — and the token is never sent to 3rd-party hosts (GitHub).
- **core:presentation** — `UiText` (`DynamicString` + `FromResource`, `@Composable fun asString()`), `fun DataError.toUiText()`, `ObserveAsEvents(events) { }`.
- **core:design-system** — `TwitturTheme { content }`.

---

## 5. The Search pilot = the TEMPLATE (done + verified)

Created files (mirror these for every new feature):

```
feature/search/domain/
  build.gradle.kts                      # plugins: kotlinMultiplatform, androidMultiplatformLibrary; commonMain: api(projects.core.domain)
  …/domain/SearchUser.kt                # domain model (id, username, fullName?, profilePicture?, bio?, kind?, country?, followers/followingCount)
  …/domain/SearchRepository.kt          # suspend fun searchUsers(query): Result<List<SearchUser>, DataError.Network>

feature/search/data/
  build.gradle.kts                      # + kotlinSerialization plugin; commonMain: api(domain), impl core.domain, core.data, ktor.client.core, kotlinx.serialization.json, koin.core
  …/data/SearchDtos.kt                  # @Serializable SearchResponseDto{users}, SearchUserDto{…}
  …/data/SearchMapper.kt                # SearchUserDto.toSearchUser()
  …/data/SearchRepositoryImpl.kt        # httpClient.get<SearchResponseDto>("search", mapOf("keyword" to query)).map { … }
  …/data/di/SearchDataModule.kt         # searchDataModule = module { single<SearchRepository> { SearchRepositoryImpl(get()) } }

feature/search/presentation/
  build.gradle.kts                      # + composeMultiplatform + composeCompiler; commonMain: api(domain), impl core.presentation, bundles.compose, bundles.lifecycleCompose, bundles.koinCompose, bundles.coil, kotlinx.coroutines.core
  …/presentation/SearchState.kt         # SearchState + SearchUserUi (Ui model) + toSearchUserUi()
  …/presentation/SearchAction.kt        # OnQueryChange / OnSearch / OnUserClick
  …/presentation/SearchEvent.kt         # NavigateToProfile / ShowError(UiText)
  …/presentation/SearchViewModel.kt     # ViewModel: onAction(), StateFlow state, Channel events, repo.searchUsers().onSuccess/onFailure → toUiText
  …/presentation/SearchScreen.kt        # SearchRoot (koinViewModel, ObserveAsEvents, snackbar) + SearchScreen (pure state+onAction) + SearchUserRow (Coil3 AsyncImage)
  …/presentation/di/SearchPresentationModule.kt   # searchPresentationModule = module { viewModelOf(::SearchViewModel) }
```

MVI pattern (per `android-presentation-mvi` skill): `State`/`Action`/`Event` + `ViewModel(onAction, StateFlow, Channel<Event>)` + **Root** composable (holds VM via `koinViewModel()`, `ObserveAsEvents`) + pure **Screen** composable (`state` + `onAction`). UI models suffixed `Ui`. Errors → `UiText` via `toUiText()`.

---

## 6. `:composeApp` foundation (done + verified)

- `composeApp/.../di/InitKoin.kt` — `initKoin(config?)`: idempotent (`if (GlobalContext.getOrNull() != null) return`) `startKoin { modules(coreDataModule, searchDataModule, searchPresentationModule) }`. **Add each new feature's data + presentation Koin modules here.**
- `composeApp/.../navigation/Destinations.kt` — `@Serializable data object SearchRoute`. **Add a route per feature.**
- `composeApp/.../App.kt` — `setSingletonImageLoaderFactory { ImageLoader.Builder(it).components { add(KtorNetworkFetcherFactory()) }.build() }` + `TwitturTheme { NavHost(startDestination = SearchRoute) { composable<SearchRoute> { SearchRoot(onNavigateToProfile = {}) } } }`. **Add a `composable<…>` per feature.**
- `composeApp/build.gradle.kts` — added `kotlinSerialization` plugin + commonMain deps: `projects.core.{data,presentation,designSystem}`, `projects.feature.search.{data,presentation}`, `libs.koin.core`, `libs.bundles.koinCompose`, `libs.bundles.coil`, `libs.navigation.compose` (already had `bundles.compose` + `bundles.lifecycleCompose`).
- Platform entrypoints call `initKoin()`: `androidApp/.../MainActivity.onCreate` (before `setContent`), `desktopApp/.../main()`, `composeApp/iosMain/.../MainViewController()`.

---

## 7. Conventions & gotchas learned the hard way

1. **No `@Preview` in feature `commonMain`** — `org.jetbrains.compose.ui.tooling.preview` is NOT on the classpath via `libs.bundles.compose`. (Removing it was the only fix needed to get the pilot compiling.) Add a preview dep if you want previews.
2. **No `Icons.*`** in commonMain — material-icons isn't in `libs.bundles.compose`. Use Coil images / plain UI, or add `material-icons-core`.
3. **UI strings are hardcoded for now.** Resource migration (`R.string`/`drawable`/`raw` → generated `Res.*` in `composeApp/.../composeResources`) is a **deferred decision** — do a dedicated pass.
4. **No Lottie** in the catalog. Track B used Airbnb Lottie for empty-states; for Track A either add **compottie** (KMP) or drop the animations. **Deferred decision.**
5. Mirror an existing `build.gradle.kts` exactly (inline KMP DSL, `JvmTarget.JVM_11`, no build-logic).
6. Type-safe project accessors are enabled: `projects.core.designSystem`, `projects.feature.search.presentation`, etc.
7. **Opening a URL / back-arrow without `Icons.*`:** use `androidx.compose.ui.platform.LocalUriHandler.current.openUri(url)` (it's in commonMain — no expect/actual needed) and a plain `TextButton { Text("Back") }` for the nav icon. Both used in `notification`'s `PatchNoteScreen`.
8. **Absolute-URL endpoints** (e.g. GitHub) — just pass the full `https://…` string as the `route` to `httpClient.get(...)`; `constructRoute()` passes it through (see Section 4).
9. **Auto-load on screen open:** `notification`'s VM uses `MutableStateFlow.onStart { load() }.stateIn(scope, WhileSubscribed(5000L), initial)` (vs. search which loads on a user action) — both are fine MVI variants.
10. **`GlobalContext` is NOT in koin-core's native artifact** — `org.koin.core.context.GlobalContext` resolves on JVM/Android but is UNRESOLVED on iOS (only top-level `startKoin`/`stopKoin` are common). `InitKoin.kt` now guards double-start with a plain `private var koinStarted` flag instead. Avoid `GlobalContext`/JVM-only Koin APIs in commonMain.
11. **POST + writing the session:** `auth`'s data layer injects `SessionSource` and, on login success, does `.onSuccess { sessionSource.setToken(...); setUserId(...) }.asEmptyResult()`. Repositories returning a side-effect-only result return `EmptyResult<DataError.Network>`.
12. **Form screens (auth):** form field state is kept LOCAL via `rememberSaveable` in the Screen (matches the Track-B reference; survives process death); the VM is thin (isLoading state + events + a submit method, no per-field Action). Password show/hide + back nav use a `TextButton("Show"/"Hide"/"Back")` (no `Icons.*`). The student "major" dropdown uses a manual `Box { OutlinedTextField(readOnly) + transparent clickable overlay + DropdownMenu }` to dodge the churny `ExposedDropdownMenuBox.menuAnchor()` API. One reg `RegistrationViewModel` is shared by both reg screens (both POST `users`).
13. **NEVER reuse a request DTO as the response type for a write whose response shape differs.** The server echoes back the created/updated entity *without* write-only fields like `password`, so deserializing into the request DTO throws `MissingFieldException` on a successful 2xx (this was the registration/profile-edit bug — see Section 0). **For fire-and-forget writes (POST/PUT/DELETE) where the body is unused, type the response as `Unit`** — Ktor's `DefaultTransform` cancels/discards the body before ContentNegotiation runs, so no deserialization happens. If you *do* need the response, give it its own all-nullable response DTO (like `UserDto`), never the request DTO. `SafeCall`'s `responseToResult` only deserializes on 2xx, so error bodies (`{"error":...}`) never hit the serializer.

---

## 8. ⭐ RECIPE: how to port the next feature (do this repeatedly)

For feature `<x>` (e.g. `notification`, `profile`, `auth`, `tweet`, `detail`, `follow`):

1. **Find the spec + endpoint** in `:app`: the Track-B screen `app/src/main/java/.../<x>/presentation/screens/*Screen.kt` (UI/behavior) and the legacy Retrofit API `app/src/main/java/.../<x>/data/remote/api/*.kt` + repo + sealed states (endpoint paths, request/response shape).
2. **`:feature:<x>:domain`** — copy search/domain build.gradle (rename namespace). Add domain model(s) + `<X>Repository` interface returning `Result<…, DataError.Network>`.
3. **`:feature:<x>:data`** — copy search/data build.gradle. `@Serializable` DTOs + mapper + `<X>RepositoryImpl(httpClient)` using `httpClient.get/post(...)` from `core.data.network`; `<x>DataModule` Koin (`single<…Repository> { …Impl(get()) }`).
4. **`:feature:<x>:presentation`** — copy search/presentation build.gradle. MVI `State`/`Action`/`Event` + `…ViewModel` + `…Root`/`…Screen` (port the Track-B screen UI, swap Coil2→Coil3 `coil3.compose.AsyncImage`, drop `Icons.*`/`@Preview`/Lottie, `R.*`→hardcode for now, LiveData→already StateFlow). `…PresentationModule` (`viewModelOf(::…ViewModel)`).
5. **Wire it up:** `include(":feature:<x>:…")` ×3 in `settings.gradle.kts`; add the data+presentation modules to `composeApp` `build.gradle.kts` deps and to `initKoin()`; add a `@Serializable …Route` in `Destinations.kt` and a `composable<…Route>` in `App.kt`'s NavHost (wire cross-feature nav via callbacks).
6. **Verify:** run ALL THREE (Section 2): `:androidApp:assembleDebug`, `:composeApp:compileKotlinJvm`, `:composeApp:linkDebugFrameworkIosSimulatorArm64` (JBR 21). Fix compile errors — **iOS too** (commonMain is checked against the iOS leaf).

Platform-specific actions (email/share/WebView/image-pick/speech) → `expect/actual` in `composeApp` (`Platform.kt` + per-platform actuals) or a small `:core:*` module.

### 8a. App-level routing (in `App.kt`) — auth-first
- `App()` does `val sessionSource = koinInject<SessionSource>()` then `val startDestination: Any = remember { if (sessionSource.isRemembered()) HomeRoute else SignInRoute }` → `NavHost(startDestination = startDestination)`. (`koinInject` from `org.koin.compose`; works because `initKoin()` runs before `App()` on every platform entry point. `startDestination` is typed `Any` since the type-safe `NavHost` accepts a route instance.)
- **Flow:** SignIn → (`onSignUp`) Kind → Student/Prof reg (`onRegistered` pops back to SignIn); SignIn → (`onSignedIn`) StayIn → (`onFinished`) Home with `popUpTo(SignInRoute){inclusive=true}`. Home → Feed / Search / Notifications / Profile; Home logout → SignIn with `popUpTo(HomeRoute){inclusive=true}`. Feed → (FAB) ComposeTweet (`onPosted` pops back to Feed); Feed `onOpenTweet` → `DetailRoute(tweetId)` (type-safe arg). Detail → reply opens its own `DetailRoute`; "N likes" → `LikesListRoute(tweetId)`. Profile → Edit; Profile logout / account-deletion → SignIn with `popUpTo(HomeRoute){inclusive=true}`.
- Adding a new top-level feature later: register its `composable<…Route>` and add an entry point from `HomeRoot` (e.g. a new `onOpenX` callback).

---

## 9. Backend

REST base `https://twitturin-api.onrender.com/api/`. Known endpoints (verified against the live API):
- **Search** = `GET search?keyword=`.
- **Auth** = `POST auth` `{username,password}` → `{id,token,username}` (404 `{"error":"user not found"}` on bad creds). **Registration** = `POST users` `{fullName,username,password,kind,...}` → echoes the created user (no `password`); `kind` ∈ `student|teacher`; students also need `studentId`+`major` (`major` ∈ enum `SE/BM/IT/ME/CIE/AD/AE`; `studentId` has an opaque server-side validator); teachers need `subject`.
- **Profile** = `GET users/{id}` (public), `PUT users/{id}` (edit), `DELETE users/{id}`.
- **Tweet** = `GET tweets` (feed → `List<Tweet>`), `GET users/{id}/tweets`, `GET users/{id}/likes`, `POST tweets` `{content}` (auth), `DELETE tweets/{id}` (auth), `PUT tweets/{id}` (edit, auth), `GET tweets/{id}` (one tweet), `GET tweets/{id}/replies`, `POST tweets/{id}/replies` (auth), `DELETE replies/{id}` (auth), `POST/DELETE tweets/{id}/likes` `{count}` (auth — murky contract, see tweet row). A tweet = `{id,content,author{id,username,fullName,profilePicture,...},createdAt,updatedAt,likes,likedBy[],replyCount,isEdited}`.

Discover anything else from the legacy Retrofit interfaces under `app/src/main/java/com/example/twitturin/*/data/remote/api/*.kt`. Auth is bearer-token; `core:data` already wires `Auth` from `SessionSource` (sent preemptively to the twitturin host only).

---

## 10. Remaining Track A work (feature areas to port, with `:app` source roots)

| Feature | `:app` source root | Notes |
|---|---|---|
| notification (GitHub releases) | `notification/` | ✅ DONE + verified (2026-06-28). `:feature:notification:{domain,data,presentation}`; absolute GitHub URL; `NotificationRoot`+`PatchNoteRoot` share one VM; `LocalUriHandler` for the Update button. |
| auth (signin / kind / student+prof registration / stayIn) | `auth/` | ✅ DONE + verified (Android/JVM/iOS, 2026-06-28). `:feature:auth:{domain,data,presentation}`. POST `auth`→`{id,token}` saved to `SessionSource`; POST `users` (student/prof). Shared `RegistrationViewModel`; `LoginViewModel`; `StayInViewModel` (writes `setRemembered`). **Now the start of the app** (auth-first routing). One remaining simplification: StayIn avatar dropped (needs profile). |
| home (hub) | `home/` | ✅ Thin hub DONE + verified (2026-06-28). `:feature:home:presentation` (presentation-only). Post-login landing: shows signed-in user id, navigates to **Feed**/Search/Notifications/Profile, logs out (`HomeViewModel.logout()` → `SessionSource.clear()`). **Still not the real feed *body* yet** — the feed is now reachable as its own `FeedRoute` (Home → "Feed"); folding it into Home directly (drawer/FAB) stays open because features can't depend on each other, so the composition must live in `:composeApp` (e.g. give `FeedRoot` top-bar action slots and render it under `HomeRoute`). |
| profile (profile / edit / delete / fullscreen) | `profile/` | ✅ DONE + verified (Android/JVM/iOS, 2026-06-28). `:feature:profile:{domain,data,presentation}`. `GET users/{id}` (public), `PUT users/{id}` (edit), `DELETE users/{id}`. Shared `ProfileViewModel(repo, sessionSource)` (userId from session); `ProfileRoot` (header + menu Edit/Logout/Delete + confirm dialogs + fullscreen avatar `Dialog`) and `EditProfileRoot`. **Deferred:** image picker/upload (the expect/actual; `POST users/{id}/profilePicture` multipart — see Open decisions); tweet/likes tabs (need `tweet`); observe-other-user + share. StayIn avatar still dropped (would need an auth→profile dep; instead show it once a session-cached avatar or a shared user store exists). |
| tweet (feed / compose / edit / likes / tweets list / report) | `tweet/` | ✅ **DONE + verified (Android/JVM/iOS, 2026-06-29)** — *core slice*. `:feature:tweet:{domain,data,presentation}`. `TweetRepository` = `getFeed` (`GET tweets`), `getUserTweets` (`GET users/{id}/tweets`), `postTweet` (`POST tweets`, auth), `deleteTweet` (`DELETE tweets/{id}`, auth). Shared `TweetItem` composable (`presentation/components`; Coil3 avatar, no `Icons.*`, engagement counts are static labels, `headerAction` slot hosts a Delete button on own tweets). `feed/` MVI (`FeedViewModel(repo, sessionSource)` — `isMine` = author.id == session userId; auto-load via `onStart`+`stateIn`; optimistic delete) + `post/` MVI (`PostTweetViewModel`, 280-char compose). Routes `FeedRoute`/`ComposeTweetRoute`; reachable Home→Feed→(FAB)Compose. **Deferred (document, don't silently drop):** like/unlike (`{count}` body contract is murky — was a "developing" stub even in Track B), edit-tweet (`PUT tweets/{id}`), report. **`detail` (tweet detail + replies + list-of-likes) is now DONE inside this module** — feed `onOpenTweet` → `DetailRoute` (see detail row). Unblocks the profile tweet/likes tabs (still to wire). |
| detail (tweet detail + replies + more-settings + list-of-likes) | `detail/` | ✅ **DONE + verified (Android/JVM/iOS, 2026-06-29)** — implemented **inside `:feature:tweet`** (not a `:feature:detail` module — honors the no-cross-feature-dep rule; detail = the tweet's own detail view, reuses `Tweet`/`TweetItem` in-module). `TweetRepository` gained `getTweet` (`GET tweets/{id}`), `getReplies` (`GET tweets/{id}/replies` → tweet-shaped), `postReply` (`POST tweets/{id}/replies`, auth), `getLikers` (`GET tweets/{id}/likes` → users + `TweetLiker`). `presentation/detail/` MVI (`DetailViewModel.load(tweetId)` fetches tweet+replies, reply draft held in state, optimistic re-fetch after reply) → `DetailRoot`/`DetailScreen` (custom header + reply bottom-bar + replies as `TweetItem`; "N likes" → likes list). `presentation/likes/` (`LikesListViewModel.load(tweetId)` → `LikesListRoot`). Routes `DetailRoute(tweetId)`/`LikesListRoute(tweetId)` (type-safe `data class` + `entry.toRoute<…>()`); **feed `onOpenTweet` now navigates to detail**; a reply opens its own detail. **Deferred:** more-settings (delete-own-tweet/report from detail), Follow button (needs `follow`), like toggle (deferred contract). |
| follow (followers / following) | `follow/` | `FollowViewModel` shared; Track B has shared `FollowComponents` |
| search | `search/` | ✅ DONE (pilot) |
| chat (stubs), settings, feedback, connection | resp. dirs | mostly simple / static |

When porting `tweet`/`home`/`detail`/`profile`/`likes`, build a shared **`TweetItem`** in (e.g.) `core:design-system` or a `feature:tweet:presentation` shared component — they all render the same tweet card (Track B already factored this in `:app`).

---

## 11. Open decisions / pending

- **Profile image picker/upload (expect/actual):** deferred. Plan: an `expect` composable image picker (Android `PickVisualMedia` + read bytes; desktop AWT `FileDialog`; iOS PHPicker — the hard, untestable-by-CLI part) returning `ByteArray`, plus a `ProfileRepository.uploadProfilePicture(userId, bytes, fileName)` doing a Ktor multipart `POST users/{id}/profilePicture` (`MultiPartFormDataContent`/`formData`) — mind that `defaultRequest { contentType(Application.Json) }` must not clobber the multipart Content-Type. Live the picker in `composeApp` or a small `:core:*` module (per the house rule), wired into `EditProfileRoot`'s avatar tap → `viewModel`.
- **Resources:** migrate `R.*` → `Res.*` (`composeResources`) — currently hardcoding pilot strings.
- **Lottie:** add `compottie` vs. drop empty-state animations.
- **iOS:** ✅ framework compiles + links (`linkDebugFrameworkIosSimulatorArm64`, 2026-06-28). Still TODO: actually open `iosApp/` in Xcode + run on a booted simulator (the Gradle-side framework is proven; the Xcode app run is not).
- **Web (Wasm):** deferred; `:webApp` not in the build. **"Run all apps" cannot include Web yet.**
- **Last user request was "run all apps" (desktop/iOS/Android/Web)** — was probing env (adb device? booted simulator?) when interrupted. Reality: Web doesn't exist; iOS needs Xcode + booted sim + native compile; Android needs a device/emulator (`adb devices`); Desktop is runnable via `./gradlew :desktopApp:run` (opens a window). Verify what's available before claiming to run them.

---

## 12. Track B (legacy `:app`) — the 39 converted screens

Still uncommitted on this branch, never compiled (`:app` excluded from the build). They are the **UI spec** for Track A — read the relevant `…/presentation/screens/*Screen.kt` when porting each feature. Dead code to delete at cutover (relay `*UiViewModel`/`*UiEvent`, RecyclerView adapters, XML layouts). Do **not** try to make `:app` compile under the new toolchain.

---

*Persistent project memory also lives at `~/.claude/.../memory/twittur-kmp-migration.md` (auto-loaded each session). Keep both in sync.*
