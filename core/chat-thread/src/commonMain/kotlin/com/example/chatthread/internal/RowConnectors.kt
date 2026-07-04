package com.example.chatthread.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * Draws the tree connector lines (ancestor guides, parent→avatar branch, own trunk) behind a row.
 *
 * All geometry is derived from [avatarSize], [indentPerLevel] and the depth metadata carried on
 * each [VisibleRow], so no magic numbers live in this file — tweak [avatarSize] or [indentPerLevel]
 * in [com.example.chatthread.ThreadStyle] and the lines stay aligned.
 *
 * Column math:
 * - A row at depth `d` places its avatar at x = `d * indentPerLevel`.
 * - An ancestor at depth `i` has its own "trunk column" (the vertical through the middle of its
 *   avatar) at x = `(i) * indent + radius`. For drawing *pass-through* guides on descendants we
 *   shift to `(i-1) * indent + radius` because on a descendant row the ancestor's avatar column
 *   corresponds to i-1 indents from the left.
 * - `connectorY` (the horizontal branch height) sits at `radius`, i.e. the vertical centre of the
 *   avatar, so every horizontal line meets the avatar's left edge cleanly.
 *
 * The draw happens in three phases:
 *   1. Ancestor pass-through guides.
 *   2. A parent-to-avatar branch — rounded L if this is the last sibling, tee (continuous trunk +
 *      short horizontal) otherwise.
 *   3. This row's own downward trunk, drawn only when expanded so the line reaches the first child
 *      with no gap.
 *
 * @param depth 0-based tree depth of this row.
 * @param ancestorHasMoreSiblings Per-depth "ancestor has later siblings" flags; see [VisibleRow].
 * @param isLastSiblingAtDepth Whether this row is the last among its siblings.
 * @param isExpanded Whether this row is currently expanded (drives phase 3).
 * @param avatarSize Avatar diameter; half of this is the radius used for geometry.
 * @param indentPerLevel Horizontal indent added per depth level.
 * @param cornerRadius Corner rounding for the last-sibling L connector; clamped to avatar radius.
 * @param strokeWidth Stroke thickness for every line.
 * @param color Line color.
 */
internal fun Modifier.threadConnectors(
    depth: Int,
    ancestorHasMoreSiblings: BooleanArray,
    isLastSiblingAtDepth: Boolean,
    isExpanded: Boolean,
    avatarSize: Dp,
    indentPerLevel: Dp,
    cornerRadius: Dp,
    strokeWidth: Dp,
    color: Color,
): Modifier = this.drawBehind {
    val indentPx = indentPerLevel.toPx()
    val radiusPx = avatarSize.toPx() / 2f
    val cornerPx = cornerRadius.toPx().coerceAtMost(radiusPx)
    val strokePx = strokeWidth.toPx()
    val stroke = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)

    val connectorY = radiusPx

    // 1. Ancestor guide lines — for each ancestor at depth i (1 <= i < depth) whose subtree has
    //    more siblings below, draw a vertical guide at that ancestor's trunk column (i-1)*indent+radius.
    //    Skip i=0 (roots have no shared parent trunk) and skip the direct parent (i=depth-1 is
    //    still included here only if i >= 1; the direct parent's own trunk column sits one level
    //    out from the L-connector column handled below, so no overlap).
    for (i in 1 until ancestorHasMoreSiblings.size) {
        if (!ancestorHasMoreSiblings[i]) continue
        val x = (i - 1) * indentPx + radiusPx
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = strokePx,
            cap = StrokeCap.Butt,
        )
    }

    // 2. Line from parent's trunk into this avatar (skipped for depth 0 roots).
    if (depth > 0) {
        val parentTrunkX = (depth - 1) * indentPx + radiusPx
        val avatarLeftX = depth * indentPx

        if (isLastSiblingAtDepth) {
            // Rounded L: trunk down to just before the turn, then bezier, then horizontal.
            val path = Path().apply {
                moveTo(parentTrunkX, 0f)
                lineTo(parentTrunkX, connectorY - cornerPx)
                quadraticTo(
                    parentTrunkX, connectorY,
                    parentTrunkX + cornerPx, connectorY,
                )
                lineTo(avatarLeftX, connectorY)
            }
            drawPath(path = path, color = color, style = stroke)
        } else {
            // Tee: continuous vertical trunk through the row, plus a horizontal branch.
            drawLine(
                color = color,
                start = Offset(parentTrunkX, 0f),
                end = Offset(parentTrunkX, size.height),
                strokeWidth = strokePx,
                cap = StrokeCap.Butt,
            )
            drawLine(
                color = color,
                start = Offset(parentTrunkX, connectorY),
                end = Offset(avatarLeftX, connectorY),
                strokeWidth = strokePx,
                cap = StrokeCap.Round,
            )
        }
    }

    // 3. If this row is expanded and has children, its own trunk extends down from the avatar
    //    to the next row. Draw it here so there's no gap between the avatar and the first child.
    if (isExpanded) {
        val ownTrunkX = depth * indentPx + radiusPx
        drawLine(
            color = color,
            start = Offset(ownTrunkX, connectorY + radiusPx),
            end = Offset(ownTrunkX, size.height),
            strokeWidth = strokePx,
            cap = StrokeCap.Butt,
        )
    }
}
