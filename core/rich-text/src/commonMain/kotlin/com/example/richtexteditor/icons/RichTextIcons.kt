package com.example.richtexteditor.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The upstream library's toolbar drawables (Material Symbols, 960 viewport) hand-ported as
 * Compose [ImageVector]s — pure commonMain, no resource pipeline, same technique as the app's
 * `TwitturIcons`. Single-colour: recolour with `Icon(tint = …)`.
 */
object RichTextIcons {

    private fun filled(path: String): ImageVector =
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            addPath(
                pathData = PathParser().parsePathString(path).toNodes(),
                fill = SolidColor(Color.Black),
            )
        }.build()

    val FormatBold: ImageVector by lazy { filled("M272,760L272,200L493,200Q558,200 613,240Q668,280 668,351Q668,402 645,429.5Q622,457 602,469L602,469Q627,480 657.5,510Q688,540 688,600Q688,689 623,724.5Q558,760 501,760L272,760ZM393,648L497,648Q545,648 555.5,623.5Q566,599 566,588Q566,577 555.5,552.5Q545,528 494,528L393,528L393,648ZM393,420L486,420Q519,420 534,403Q549,386 549,365Q549,341 532,326Q515,311 488,311L393,311L393,420Z") }
    val FormatItalic: ImageVector by lazy { filled("M200,760L200,660L360,660L480,300L320,300L320,200L720,200L720,300L580,300L460,660L600,660L600,760L200,760Z") }
    val FormatUnderlined: ImageVector by lazy { filled("M200,840L200,760L760,760L760,840L200,840ZM480,680Q379,680 323,617Q267,554 267,450L267,120L370,120L370,456Q370,512 398,547Q426,582 480,582Q534,582 562,547Q590,512 590,456L590,120L693,120L693,450Q693,554 637,617Q581,680 480,680Z") }
    val ArrowDown: ImageVector by lazy { filled("M480,616L240,376L296,320L480,504L664,320L720,376L480,616Z") }
    val ArrowUp: ImageVector by lazy { filled("M480,344L240,584L296,640L480,456L664,640L720,584L480,344Z") }
    val MoreVert: ImageVector by lazy { filled("M480,800Q447,800 423.5,776.5Q400,753 400,720Q400,687 423.5,663.5Q447,640 480,640Q513,640 536.5,663.5Q560,687 560,720Q560,753 536.5,776.5Q513,800 480,800ZM480,560Q447,560 423.5,536.5Q400,513 400,480Q400,447 423.5,423.5Q447,400 480,400Q513,400 536.5,423.5Q560,447 560,480Q560,513 536.5,536.5Q513,560 480,560ZM480,320Q447,320 423.5,296.5Q400,273 400,240Q400,207 423.5,183.5Q447,160 480,160Q513,160 536.5,183.5Q560,207 560,240Q560,273 536.5,296.5Q513,320 480,320Z") }
    val Replay: ImageVector by lazy { filled("M480,880Q405,880 339.5,851.5Q274,823 225.5,774.5Q177,726 148.5,660.5Q120,595 120,520L200,520Q200,637 281.5,718.5Q363,800 480,800Q597,800 678.5,718.5Q760,637 760,520Q760,403 678.5,321.5Q597,240 480,240L474,240L536,302L480,360L320,200L480,40L536,98L474,160L480,160Q555,160 620.5,188.5Q686,217 734.5,265.5Q783,314 811.5,379.5Q840,445 840,520Q840,595 811.5,660.5Q783,726 734.5,774.5Q686,823 620.5,851.5Q555,880 480,880Z") }
    val Check: ImageVector by lazy { filled("M382,720L154,492L211,435L382,606L749,239L806,296L382,720Z") }
    val TextFormat: ImageVector by lazy { filled("M200,760L200,680L760,680L760,760L200,760ZM276,600L440,160L520,160L684,600L608,600L570,488L392,488L352,600L276,600ZM414,424L546,424L482,242L478,242L414,424Z") }
}
