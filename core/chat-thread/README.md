# :core:chat-thread — vendored CollapsibleChatThread

This module is a **verbatim vendor** of the `chatthread` library module from
[extractive-mana-pulse/chat_thread_lib](https://github.com/extractive-mana-pulse/chat_thread_lib)
(package `com.example.chatthread`, same public API).

**Why vendored instead of the JitPack dependency:** the published artifact
(`com.github.extractive-mana-pulse:chatthread`) is an Android-only AAR, but this project's
feature UI lives in KMP `commonMain` (Android / iOS / Desktop). The library source is pure
Compose (no Android imports), so compiling it in `commonMain` makes it usable on every platform.

- `src/commonMain/.../chatthread/*` — byte-identical copies of the upstream sources. **Do not
  edit them here**; change upstream and re-copy, so the two stay diffable.
- `src/commonTest/.../ThreadFlattenerTest.kt` — the upstream JUnit4 test ported to `kotlin.test`
  (same cases, multiplatform runner).

To sync with upstream:

```sh
git clone https://github.com/extractive-mana-pulse/chat_thread_lib /tmp/ctl
cp /tmp/ctl/chatthread/src/main/java/com/example/chatthread/*.kt \
   core/chat-thread/src/commonMain/kotlin/com/example/chatthread/
cp /tmp/ctl/chatthread/src/main/java/com/example/chatthread/internal/*.kt \
   core/chat-thread/src/commonMain/kotlin/com/example/chatthread/internal/
```

Used by `:feature:tweet:presentation` to render the tweet-detail reply thread.
