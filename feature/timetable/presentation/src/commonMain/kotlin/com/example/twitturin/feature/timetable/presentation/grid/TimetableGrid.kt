package com.example.twitturin.feature.timetable.presentation.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.DividerLine
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.designsystem.theme.SurfaceMuted
import com.example.twitturin.feature.timetable.presentation.TimetableBlockUi
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime

private val HourRailWidth = 44.dp
private val MinuteHeight = 1.3.dp // ~78dp/hour — enough for a block to fit subject + room + teacher
private const val DEFAULT_START_MINUTE = 8 * 60
private const val DEFAULT_END_MINUTE = 20 * 60

/**
 * The Day/3-Day/Week grid: a sticky hour rail beside one column per visible date, sharing a
 * single vertical scroll. Blocks are positioned by absolute offset from each period's real clock
 * time (not equal-height rows — this institution's periods have a lunch-length gap between
 * periods 3 and 4, so equal rows would misrepresent the actual gaps).
 */
@Composable
fun TimetableGrid(
    windowDates: List<LocalDate>,
    blocksByDate: Map<LocalDate, List<TimetableBlockUi>>,
    onLessonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val allBlocks = remember(blocksByDate) { blocksByDate.values.flatten() }
    val startMinute = remember(allBlocks) {
        (allBlocks.minOfOrNull { it.startMinutesOfDay } ?: DEFAULT_START_MINUTE).coerceAtMost(DEFAULT_START_MINUTE)
    }
    val endMinute = remember(allBlocks) {
        (allBlocks.maxOfOrNull { it.endMinutesOfDay } ?: DEFAULT_END_MINUTE).coerceAtLeast(DEFAULT_END_MINUTE)
    }
    val bodyHeight = MinuteHeight * (endMinute - startMinute)

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Spacer(modifier = Modifier.width(HourRailWidth))
            windowDates.forEach { date ->
                DayHeader(date = date, isToday = date == today, modifier = Modifier.weight(1f))
            }
        }
        HorizontalDivider(color = DividerLine)
        val scrollState = rememberScrollState()
        Row(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            HourRail(
                startMinute = startMinute,
                endMinute = endMinute,
                modifier = Modifier.width(HourRailWidth).height(bodyHeight),
            )
            windowDates.forEach { date ->
                DayColumn(
                    isToday = date == today,
                    blocks = blocksByDate[date].orEmpty(),
                    startMinute = startMinute,
                    endMinute = endMinute,
                    bodyHeight = bodyHeight,
                    onLessonClick = onLessonClick,
                    modifier = Modifier.weight(1f).height(bodyHeight),
                )
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate, isToday: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = date.dayOfWeek.name.take(3),
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) Brand else SecondaryText,
        )
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(28.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isToday) Brand else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.day.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isToday) OnBrand else Ink,
            )
        }
    }
}

@Composable
private fun HourRail(startMinute: Int, endMinute: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val hours = ((startMinute / 60) + 1)..(endMinute / 60)
        for (hour in hours) {
            Text(
                text = hour.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                modifier = Modifier
                    .offset(y = MinuteHeight * (hour * 60 - startMinute) - 7.dp)
                    .padding(end = 6.dp),
            )
        }
    }
}

@Composable
private fun DayColumn(
    isToday: Boolean,
    blocks: List<TimetableBlockUi>,
    startMinute: Int,
    endMinute: Int,
    bodyHeight: Dp,
    onLessonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Two followed subjects can share the same day + period (e.g. a lecture and a lab a student
    // follows both of). Rather than stacking them at the same offset — which paints their labels
    // on top of each other — split each overlap cluster into side-by-side lanes.
    val positioned = remember(blocks) { assignLanes(blocks) }
    BoxWithConstraints(modifier = modifier.background(if (isToday) SurfaceMuted else Color.Transparent)) {
        val columnWidth = maxWidth
        positioned.forEach { placed ->
            val block = placed.block
            val laneWidth = columnWidth / placed.laneCount
            val top = MinuteHeight * (block.startMinutesOfDay - startMinute)
            val height = (MinuteHeight * (block.endMinutesOfDay - block.startMinutesOfDay)).coerceAtLeast(52.dp)
            LessonBlock(
                block = block,
                onClick = { onLessonClick(block.occurrenceId) },
                modifier = Modifier
                    .offset(x = laneWidth * placed.lane, y = top)
                    .width(laneWidth * placed.span)
                    .height(height)
                    .padding(horizontal = 2.dp, vertical = 1.dp),
            )
        }
        if (isToday) {
            NowLine(startMinute = startMinute, endMinute = endMinute)
        }
    }
}

private data class PlacedBlock(val block: TimetableBlockUi, val lane: Int, val laneCount: Int, val span: Int)

/**
 * Greedy interval-graph lane assignment. Blocks are grouped into clusters of transitively
 * time-overlapping lessons; within a cluster each block takes the first lane free at its start,
 * and the cluster's lane count fixes every member's width so lanes line up. A block with no
 * neighbour in a later lane [span]s across the free ones so a lone lesson still fills the column.
 */
private fun assignLanes(blocks: List<TimetableBlockUi>): List<PlacedBlock> {
    if (blocks.isEmpty()) return emptyList()
    val sorted = blocks.sortedWith(compareBy({ it.startMinutesOfDay }, { it.endMinutesOfDay }))
    val placed = mutableListOf<PlacedBlock>()
    var index = 0
    while (index < sorted.size) {
        var clusterEnd = sorted[index].endMinutesOfDay
        val cluster = mutableListOf(sorted[index])
        var next = index + 1
        while (next < sorted.size && sorted[next].startMinutesOfDay < clusterEnd) {
            cluster += sorted[next]
            clusterEnd = maxOf(clusterEnd, sorted[next].endMinutesOfDay)
            next++
        }
        val laneEnds = mutableListOf<Int>() // last occupied end-minute per lane
        val laneByBlock = LinkedHashMap<TimetableBlockUi, Int>()
        for (block in cluster) {
            var lane = laneEnds.indexOfFirst { it <= block.startMinutesOfDay }
            if (lane == -1) {
                lane = laneEnds.size
                laneEnds += block.endMinutesOfDay
            } else {
                laneEnds[lane] = block.endMinutesOfDay
            }
            laneByBlock[block] = lane
        }
        val laneCount = laneEnds.size
        for ((block, lane) in laneByBlock) {
            // Span across any adjacent lanes that no other block in this cluster overlaps in.
            var span = 1
            while (lane + span < laneCount && cluster.none { other ->
                    other !== block && laneByBlock[other] == lane + span &&
                        other.startMinutesOfDay < block.endMinutesOfDay && block.startMinutesOfDay < other.endMinutesOfDay
                }
            ) span++
            placed += PlacedBlock(block, lane, laneCount, span)
        }
        index = next
    }
    return placed
}

/**
 * A single lesson in a day column. The block shows as much as its height allows, top-down:
 * subject name (always), then the classroom label, teacher, and finally the time range — each
 * gated on there being enough vertical space so a short lesson still renders cleanly rather than
 * clipping mid-line. The full set is always available by tapping through to the detail sheet.
 */
@Composable
private fun LessonBlock(block: TimetableBlockUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = subjectColorFor(block.subjectId)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.14f))
            .clickable(onClick = onClick),
    ) {
        Box(modifier = Modifier.fillMaxHeight().width(3.dp).background(color))
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val available = maxHeight
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp)) {
                Text(
                    text = block.subjectShortName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    maxLines = if (available >= 84.dp) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (block.roomNames.isNotEmpty()) {
                    Text(
                        text = block.roomNames,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
                if (available >= 96.dp && block.teacherNames.isNotEmpty()) {
                    Text(
                        text = block.teacherNames,
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (available >= 118.dp) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${block.startLabel}–${block.endLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun NowLine(startMinute: Int, endMinute: Int) {
    var nowMinuteOfDay by remember { mutableStateOf(currentMinuteOfDay()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            nowMinuteOfDay = currentMinuteOfDay()
        }
    }
    if (nowMinuteOfDay in startMinute..endMinute) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = MinuteHeight * (nowMinuteOfDay - startMinute))
                .background(Danger),
        )
    }
}

private fun currentMinuteOfDay(): Int {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return now.hour * 60 + now.minute
}
