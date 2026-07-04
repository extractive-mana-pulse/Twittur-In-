package com.example.chatthread

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatthread.internal.CommentRow
import com.example.chatthread.internal.flattenThread

/**
 * Holder for the set of currently-expanded comment ids.
 *
 * Hoisted state: create this once via [rememberThreadState] and pass it into
 * [CollapsibleChatThread]. Keeping expansion outside of the composable lets you drive it from
 * outside (e.g. expand-all buttons, deep links) and survives configuration changes via the
 * companion [Saver].
 *
 * The state is intentionally a `Set<String>` of ids rather than a tree-shaped structure: the
 * flattener consults it per-row, so collapsing an ancestor automatically hides descendants even if
 * the descendants are still in [expandedIds]. That means you never have to walk the tree to
 * "clean up" child ids when a parent collapses.
 */
@Stable
class ThreadState internal constructor(initial: Set<String>) {
    /** Ids of every comment whose replies should be visible. Mutated via [toggle]/[expand]/[collapse]. */
    var expandedIds: Set<String> by mutableStateOf(initial)
        internal set

    /** Returns true if the comment with [id] is currently expanded. */
    fun isExpanded(id: String): Boolean = id in expandedIds

    /** Flip the expanded state for [id]. */
    fun toggle(id: String) {
        expandedIds = if (id in expandedIds) expandedIds - id else expandedIds + id
    }

    /** Mark [id] as expanded (no-op if already expanded). */
    fun expand(id: String) {
        if (id !in expandedIds) expandedIds = expandedIds + id
    }

    /** Mark [id] as collapsed (no-op if already collapsed). */
    fun collapse(id: String) {
        if (id in expandedIds) expandedIds = expandedIds - id
    }

    companion object {
        /**
         * Saves [expandedIds] as a `List<String>` so expansion survives configuration changes
         * (rotation, process death) when wrapped by [rememberSaveable].
         */
        val Saver: Saver<ThreadState, List<String>> = Saver(
            save = { it.expandedIds.toList() },
            restore = { ThreadState(it.toSet()) },
        )
    }
}

/**
 * Creates (or restores) a [ThreadState] that survives configuration changes.
 *
 * @param initiallyExpanded Ids to expand on first composition. Typical usage: pass the root
 *   comment id so the thread opens up by default.
 */
@Composable
fun rememberThreadState(
    initiallyExpanded: Set<String> = emptySet(),
): ThreadState = rememberSaveable(saver = ThreadState.Saver) {
    ThreadState(initiallyExpanded)
}

/**
 * Renders a scrollable, collapsible, infinitely-nestable comment thread.
 *
 * The composable flattens the incoming [comments] tree into a depth-annotated row list and feeds
 * that list into a [LazyColumn], so the cost scales with what is currently *visible*, not with the
 * total tree size. Each row draws its own connector lines (trunks, tees, rounded-L's) via a custom
 * `Modifier`, which means rows are self-contained — they never measure their siblings or parent.
 *
 * Example:
 * ```
 * val state = rememberThreadState(initiallyExpanded = setOf(rootId))
 * CollapsibleChatThread(
 *     comments = myComments,
 *     state = state,
 *     style = ThreadStyle.Default.copy(
 *         expandIcon = { painterResource(R.drawable.plus) },
 *         collapseIcon = { painterResource(R.drawable.minus) },
 *         replyIcon = { painterResource(R.drawable.reply) },
 *     ),
 *     onReplyClick = { comment -> /* open composer */ },
 * )
 * ```
 *
 * @param comments Root comments. Each may carry any depth of [ThreadComment.replies].
 * @param modifier Outer modifier applied to the backing [LazyColumn].
 * @param style Visual configuration; use [ThreadStyle.Default] or `.copy(...)` it.
 * @param state Hoisted expansion state. Defaults to a fresh empty state from [rememberThreadState].
 * @param contentPadding Forwarded to [LazyColumn] — use this to pad around system bars, FABs, etc.
 * @param onReplyClick Invoked when a row's "Reply" action is tapped. The tapped [ThreadComment] is
 *   passed in so the caller can open a composer scoped to that comment.
 */
@Composable
fun CollapsibleChatThread(
    comments: List<ThreadComment>,
    modifier: Modifier = Modifier,
    style: ThreadStyle = ThreadStyle.Default,
    state: ThreadState = rememberThreadState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onReplyClick: (ThreadComment) -> Unit = {},
) {
    val visibleRows = remember(comments, state.expandedIds) {
        flattenThread(comments, state.expandedIds)
    }
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(
            items = visibleRows,
            key = { row -> row.comment.id + ":" + row.depth },
        ) { row ->
            CommentRow(
                row = row,
                style = style,
                onToggle = { state.toggle(row.comment.id) },
                onReplyClick = onReplyClick,
            )
        }
    }
}
