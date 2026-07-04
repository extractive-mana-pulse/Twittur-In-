# :core:rich-text — vendored RichTextEditor

Vendored from **[extractive-mana-pulse/RichTextEditor](https://github.com/extractive-mana-pulse/RichTextEditor)**
(the author's own library). Upstream is an Android-only demo app; this module compiles the
`rich_text_editor` package as **commonMain** so the editor runs on Android + iOS + Desktop.

The span model (`FormattingSpan` over plain text), the selection/insertion/deletion helpers in
`helper/`, the toolbar reducer, and the `BasicTextField`-with-`AnnotatedString`-overlay technique
are upstream's, kept verbatim wherever possible.

## Deviations from upstream (all deliberate)

| Upstream | Here | Why |
|---|---|---|
| `RichTextEditorViewModel` (androidx lifecycle) | `RichTextEditorController` + `rememberRichTextEditorController()` | Host screens already have MVI ViewModels; the editor document (text + spans) must be readable/clearable by the host to actually submit it. The `onAction` reducer body is the upstream ViewModel's `when` verbatim. |
| `R.drawable.*` toolbar icons | `icons/RichTextIcons.kt` ImageVectors (`PathParser`, same technique as `TwitturIcons`) | No Android resource pipeline in commonMain. |
| `R.font.*` (`TextFontFamily.fontResource: Int`) | `composeResources/font/*.ttf` + `rememberRichTextFonts()` | Same fonts, KMP resource pipeline. |
| `TextColor.Black` / `Montserrat` / `Medium` defaults | Each enum gains a **`Default` = "inherit"** first entry | Fixed black text breaks dark mode; forcing Montserrat/18sp would override the app's DM Sans body style. `Default` renders exactly like the surrounding text. |
| Hardcoded light greys (divider, toggle backgrounds, cursor) | `MaterialTheme.colorScheme` equivalents | Dark-mode + accent support. |
| Editor state lives inside `RichTextField` | Hoisted into the controller | Needed to encode/clear the document from the host screen. |

## Twittur additions (not upstream)

- **`RichTextCodec.kt`** — wire format. The backend stores plain strings, so formatting is
  serialized inline: `<rt b i u c=red f=serif s=lg>styled</rt>`. Unformatted documents encode
  **byte-identical** to their plain text, and legacy plain tweets decode unchanged (a string
  without well-formed `<rt …>…</rt>` markup is treated as plain text). `&`/`<` are escaped only
  inside formatted documents.
- **`RichTextRendering.kt`** — `rememberRichText(encoded)` / `renderRichText(encoded, fonts)`
  decode a stored string to a styled `AnnotatedString` for read-only display (feed, detail,
  reply thread).
