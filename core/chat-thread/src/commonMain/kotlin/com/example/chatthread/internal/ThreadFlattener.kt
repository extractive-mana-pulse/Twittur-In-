package com.example.chatthread.internal

import androidx.compose.runtime.Immutable
import com.example.chatthread.ThreadComment

/**
 * One row produced by [flattenThread]. Carries everything a row needs to render itself without
 * looking at its neighbours.
 *
 * @property comment The underlying comment being shown on this row.
 * @property depth 0 for root comments, 1 for their replies, and so on.
 * @property ancestorHasMoreSiblings For each ancestor at depth i, `true` iff that ancestor has
 *   later siblings. Consumed by the connector renderer to decide where to draw vertical
 *   "pass-through" guide lines so the tree reads correctly at arbitrary depth.
 * @property isLastSiblingAtDepth True when this row has no later siblings at its own depth.
 *   Controls rounded-L (last) vs tee (not last) connector shape.
 * @property hasReplies Whether this comment has any replies (regardless of expansion state). Drives
 *   whether the "Show N replies" chip is shown.
 * @property isExpanded Whether this comment is currently expanded. Drives the own-trunk line
 *   segment that descends from this row's avatar to the first child.
 */
@Immutable
internal data class VisibleRow(
    val comment: ThreadComment,
    val depth: Int,
    val ancestorHasMoreSiblings: BooleanArray,
    val isLastSiblingAtDepth: Boolean,
    val hasReplies: Boolean,
    val isExpanded: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VisibleRow) return false
        return comment.id == other.comment.id &&
            depth == other.depth &&
            isLastSiblingAtDepth == other.isLastSiblingAtDepth &&
            hasReplies == other.hasReplies &&
            isExpanded == other.isExpanded &&
            ancestorHasMoreSiblings.contentEquals(other.ancestorHasMoreSiblings)
    }

    override fun hashCode(): Int {
        var result = comment.id.hashCode()
        result = 31 * result + depth
        result = 31 * result + ancestorHasMoreSiblings.contentHashCode()
        result = 31 * result + isLastSiblingAtDepth.hashCode()
        result = 31 * result + hasReplies.hashCode()
        result = 31 * result + isExpanded.hashCode()
        return result
    }
}

/**
 * Walks the comment tree in depth-first order and returns the rows that should currently be
 * visible, each annotated with the metadata needed to draw connectors in isolation.
 *
 * Collapsing happens implicitly: children are only appended when their parent is in [expandedIds],
 * so collapsing an ancestor automatically hides every descendant in one step, even if descendants
 * remain in the expanded set.
 *
 * @param roots Top-level comments.
 * @param expandedIds Ids whose replies should be visible.
 * @return Flat list of rows in rendering order.
 */
internal fun flattenThread(
    roots: List<ThreadComment>,
    expandedIds: Set<String>,
): List<VisibleRow> {
    val out = ArrayList<VisibleRow>(roots.size)
    val ancestorStack = ArrayList<Boolean>()
    flattenInto(roots, expandedIds, out, ancestorStack)
    return out
}

/**
 * Recursive worker for [flattenThread].
 *
 * Uses an explicit [ancestorStack] so each row records a snapshot of "does each of my ancestors
 * have more siblings below?" — that snapshot is precisely what the connector renderer needs to
 * know to draw pass-through guide lines without cross-row measurement.
 *
 * The `!isLast` push before descending is the key: when we walk into a child, the parent's
 * remaining-siblings flag becomes the child's ancestor flag. Popping after descent preserves the
 * invariant that `ancestorStack.size == current depth`.
 */
private fun flattenInto(
    siblings: List<ThreadComment>,
    expandedIds: Set<String>,
    out: MutableList<VisibleRow>,
    ancestorStack: MutableList<Boolean>,
) {
    val lastIndex = siblings.lastIndex
    siblings.forEachIndexed { index, comment ->
        val isLast = index == lastIndex
        val hasReplies = comment.replies.isNotEmpty()
        val isExpanded = hasReplies && comment.id in expandedIds
        out.add(
            VisibleRow(
                comment = comment,
                depth = ancestorStack.size,
                ancestorHasMoreSiblings = ancestorStack.toBooleanArray(),
                isLastSiblingAtDepth = isLast,
                hasReplies = hasReplies,
                isExpanded = isExpanded,
            )
        )
        if (isExpanded) {
            ancestorStack.add(!isLast)
            flattenInto(comment.replies, expandedIds, out, ancestorStack)
            ancestorStack.removeAt(ancestorStack.lastIndex)
        }
    }
}
