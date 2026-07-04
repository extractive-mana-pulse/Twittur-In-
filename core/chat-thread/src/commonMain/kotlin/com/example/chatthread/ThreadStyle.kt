package com.example.chatthread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Visual configuration for [CollapsibleChatThread].
 *
 * Every dimension, color, text style, and icon the thread draws is sourced from this data class,
 * so a single `ThreadStyle.Default.copy(...)` is enough to re-skin the whole component. Dimensions
 * are used both for layout *and* for connector math, so changing [avatarSize] or [indentPerLevel]
 * keeps the tree lines aligned automatically — no magic numbers baked into the drawing code.
 *
 * Icons are exposed as `@Composable () -> Painter?` lambdas so the library stays free of Android
 * resource dependencies: callers pass `painterResource(R.drawable.foo)` (or return `null` to draw
 * a text-only chip).
 *
 * @property avatarSize Diameter of the circular avatar; also drives connector column positions.
 * @property avatarContentColor Text color for the avatar's initial character.
 * @property connectorColor Color used for every tree line (vertical trunks, horizontal branches, rounded L's).
 * @property connectorStrokeWidth Stroke width of tree lines.
 * @property connectorCornerRadius Corner rounding for the "last sibling" L-connector; clamped to the avatar radius.
 * @property indentPerLevel Horizontal indent added per nesting level; also drives trunk column spacing.
 * @property rowVerticalSpacing Space below each row, reserved inside the row so drawn connectors can span it.
 * @property avatarToContentSpacing Gap between the avatar and the text column.
 * @property contentPaddingEnd Right-side padding for the text column.
 * @property contentVerticalGap Base vertical gap used between text blocks, tag padding, and action icon spacing.
 * @property primaryTextColor Color for author name, title, body.
 * @property secondaryTextColor Color for timestamp, channel, and body.
 * @property replyText Label for the reply action (localize here).
 * @property authorStyle Typography for the author name.
 * @property timestampStyle Typography for the timestamp fragment on the author line.
 * @property channelStyle Typography for the optional channel/category line.
 * @property tagStyle Typography for badge text inside [ThreadTag] pills.
 * @property titleStyle Typography for [ThreadComment.title].
 * @property bodyStyle Typography for [ThreadComment.body].
 * @property actionStyle Typography for the "Show N replies" / "Reply" buttons.
 * @property avatarTextStyle Typography for the single initial rendered inside the avatar.
 * @property expandIcon Painter for the "expand" affordance on collapsible rows. Return `null` for text-only.
 * @property collapseIcon Painter shown while a row is expanded.
 * @property replyIcon Painter for the reply action.
 */
@Immutable
data class ThreadStyle(
    val avatarSize: Dp = 28.dp,
    val avatarContentColor: Color = Color(0xFF0F0F18),
    val connectorColor: Color = Color.White.copy(alpha = 0.3f),
    val connectorStrokeWidth: Dp = 2.dp,
    val connectorCornerRadius: Dp = 12.dp,
    val indentPerLevel: Dp = 28.dp,
    val rowVerticalSpacing: Dp = 16.dp,
    val avatarToContentSpacing: Dp = 12.dp,
    val contentPaddingEnd: Dp = 16.dp,
    val contentVerticalGap: Dp = 6.dp,
    val primaryTextColor: Color = Color.White,
    val secondaryTextColor: Color = Color(0xFFAFB2B9),
    val replyText: String = "Reply",
    val authorStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.W600,
    ),
    val timestampStyle: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.W500,
    ),
    val channelStyle: TextStyle = TextStyle(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.W500,
    ),
    val tagStyle: TextStyle = TextStyle(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.W500,
    ),
    val titleStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.W700,
    ),
    val bodyStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.W400,
    ),
    val actionStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.W500,
    ),
    val avatarTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        fontWeight = FontWeight.W700,
    ),
    val expandIcon: @Composable () -> Painter? = { null },
    val collapseIcon: @Composable () -> Painter? = { null },
    val replyIcon: @Composable () -> Painter? = { null },
) {
    companion object {
        /** Library defaults — dark-theme friendly. Use `ThreadStyle.Default.copy(...)` to tweak. */
        val Default = ThreadStyle()
    }
}
