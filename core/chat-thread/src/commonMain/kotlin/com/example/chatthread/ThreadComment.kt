package com.example.chatthread

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * A single comment in a collapsible chat thread.
 *
 * The tree is formed by [replies]: each child is itself a [ThreadComment] and may hold its own
 * replies to any depth. The library does not care where the data comes from — build the tree from
 * a network response, a database, or a hard-coded demo list.
 *
 * [id] must be unique across the whole tree. It is used as the LazyColumn item key and as the key
 * into [ThreadState.expandedIds], so two rows with the same id will confuse state and animations.
 *
 * [avatarInitial] defaults to the first character of [author]; override it if you want something
 * else (e.g. emoji, a two-letter pair, or a localized initial).
 *
 * @property id Stable unique identifier for this comment.
 * @property author Display name shown on the header line.
 * @property avatarBackground Background color of the circular avatar.
 * @property timestamp Free-form time string — the library does not format it.
 * @property body Main comment text.
 * @property avatarInitial Character(s) rendered inside the avatar circle.
 * @property channel Optional secondary line under the author (e.g. a category tag).
 * @property title Optional bold title above the body (used for the root post in the demo).
 * @property tags Optional list of small colored badges shown above the title/body.
 * @property replies Nested replies. Empty means this is a leaf.
 */
@Immutable
data class ThreadComment(
    val id: String,
    val author: String,
    val avatarBackground: Color,
    val timestamp: String,
    val body: String,
    val avatarInitial: String = author.firstOrNull()?.toString().orEmpty(),
    val channel: String? = null,
    val title: String? = null,
    val tags: List<ThreadTag> = emptyList(),
    val replies: List<ThreadComment> = emptyList(),
)

/**
 * A small colored badge shown next to the comment's author line.
 *
 * @property text Label text displayed inside the badge.
 * @property background Pill background color.
 * @property contentColor Text color drawn on top of [background].
 */
@Immutable
data class ThreadTag(
    val text: String,
    val background: Color,
    val contentColor: Color,
)
