package com.example.twitturin.feature.timetable.presentation.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.feature.timetable.presentation.TimetableWindowing
import kotlinx.datetime.LocalDate

/**
 * Month calendar grid: a conventional Mon-start 7-column week (the underlying data has no Sunday
 * lessons, but a calendar reading as 6 columns wouldn't look like a calendar — the Sunday column
 * just never carries a dot). Each cell shows a dot for how many followed lessons that weekday
 * carries; tapping a day jumps to Day view for that date. [dots] is date -> lesson count.
 */
@Composable
fun TimetableMonthCalendar(
    monthAnchor: LocalDate,
    dots: Map<LocalDate, Int>,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dates = TimetableWindowing.monthDates(monthAnchor)
    // DayOfWeek.ordinal is 0 for MONDAY..6 for SUNDAY — how many blank cells precede day 1 in a
    // Mon-start grid.
    val leadingBlanks = dates.firstOrNull()?.dayOfWeek?.ordinal ?: 0

    Column(modifier = modifier) {
        Row {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        ) {
            items(leadingBlanks) { Box(modifier = Modifier.aspectRatio(1f)) }
            items(dates) { date ->
                MonthDayCell(
                    date = date,
                    lessonCount = dots[date] ?: 0,
                    isToday = date == today,
                    onClick = { onDayClick(date) },
                )
            }
        }
    }
}

@Composable
private fun MonthDayCell(date: LocalDate, lessonCount: Int, isToday: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isToday) Brand else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) OnBrand else Ink,
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(5.dp)
                .clip(RoundedCornerShape(50))
                .background(if (lessonCount > 0) Brand else Color.Transparent),
        )
    }
}
