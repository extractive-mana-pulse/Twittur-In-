package com.example.chatthread.internal

import androidx.compose.ui.graphics.Color
import com.example.chatthread.ThreadComment
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [flattenThread].
 *
 * Ported from the upstream library's JUnit4 test to `kotlin.test` so it runs on every KMP target
 * (same cases, same assertions). The flattener is the one piece of pure logic in the library —
 * every row's rendering decision flows from the [VisibleRow] metadata it produces.
 */
class ThreadFlattenerTest {

    /** Short-hand builder for a comment with only an id and children — cuts noise in test cases. */
    private fun c(id: String, vararg replies: ThreadComment) = ThreadComment(
        id = id,
        author = id,
        avatarBackground = Color.Red,
        timestamp = "",
        body = "",
        replies = replies.toList(),
    )

    /**
     * With no ids in `expandedIds`, only the root comments are visible, depth is 0, and the
     * last-sibling flag correctly differentiates the two roots.
     */
    @Test
    fun fullyCollapsed_onlyRootsVisible() {
        val roots = listOf(
            c("a", c("a1"), c("a2")),
            c("b", c("b1")),
        )
        val rows = flattenThread(roots, expandedIds = emptySet())

        assertEquals(listOf("a", "b"), rows.map { it.comment.id })
        assertEquals(0, rows[0].depth)
        assertContentEquals(booleanArrayOf(), rows[0].ancestorHasMoreSiblings)
        assertFalse(rows[0].isLastSiblingAtDepth)
        assertTrue(rows[1].isLastSiblingAtDepth)
        assertTrue(rows[0].hasReplies)
        assertFalse(rows[0].isExpanded)
    }

    /**
     * Expanding a single root exposes its children with depth 1. Each child's
     * `ancestorHasMoreSiblings[0]` is `false` because the root has no later siblings, and the
     * last-sibling flag only fires on the final child.
     */
    @Test
    fun singleRootExpanded_revealsChildrenWithCorrectDepthMetadata() {
        val roots = listOf(
            c("root", c("a"), c("b"), c("c"))
        )
        val rows = flattenThread(roots, expandedIds = setOf("root"))

        assertEquals(listOf("root", "a", "b", "c"), rows.map { it.comment.id })
        assertEquals(1, rows[1].depth)
        assertEquals(1, rows[2].depth)
        assertEquals(1, rows[3].depth)
        // Children see root's !isLast=false (root is solo).
        assertContentEquals(booleanArrayOf(false), rows[1].ancestorHasMoreSiblings)
        assertFalse(rows[1].isLastSiblingAtDepth)
        assertFalse(rows[2].isLastSiblingAtDepth)
        assertTrue(rows[3].isLastSiblingAtDepth)
    }

    /**
     * Verifies the ancestor-stack snapshot at arbitrary depth. For a three-level expansion, the
     * `ancestorHasMoreSiblings` vector must reflect the "does this ancestor have later siblings?"
     * answer for every enclosing level — this is what drives correct pass-through guide lines.
     */
    @Test
    fun nestedExpansion_ancestorFlagsReflectSiblingPresence() {
        // root -> [A, B]; A -> [A1, A2]; A2 -> [A2a]
        val roots = listOf(
            c(
                "root",
                c("A", c("A1"), c("A2", c("A2a"))),
                c("B"),
            ),
        )
        val rows = flattenThread(roots, expandedIds = setOf("root", "A", "A2"))

        assertEquals(
            listOf("root", "A", "A1", "A2", "A2a", "B"),
            rows.map { it.comment.id },
        )

        val a1 = rows.first { it.comment.id == "A1" }
        // A1 is at depth 2: anc[0]=root's !isLast=false; anc[1]=A's !isLast=true (B follows A).
        assertContentEquals(booleanArrayOf(false, true), a1.ancestorHasMoreSiblings)
        assertFalse(a1.isLastSiblingAtDepth)

        val a2a = rows.first { it.comment.id == "A2a" }
        // A2a depth 3: anc = [false (root solo), true (A has B), false (A2 is last of A's children)].
        assertContentEquals(booleanArrayOf(false, true, false), a2a.ancestorHasMoreSiblings)
        assertTrue(a2a.isLastSiblingAtDepth)

        val b = rows.first { it.comment.id == "B" }
        assertEquals(1, b.depth)
        assertTrue(b.isLastSiblingAtDepth)
    }

    /**
     * Collapsing is implicit: if the parent is not in `expandedIds`, its children never appear
     * even when the children's own ids remain in the set. That means UI code never has to walk
     * the tree to "clean up" expanded descendants when a parent collapses.
     */
    @Test
    fun collapsingDoesNotRemoveExpandedStateOfDescendants() {
        // Collapsing A should hide A's children even if A is still in expandedIds.
        val roots = listOf(c("root", c("A", c("A1"))))
        val rows = flattenThread(roots, expandedIds = setOf("A")) // root NOT expanded
        assertEquals(listOf("root"), rows.map { it.comment.id })
    }
}
