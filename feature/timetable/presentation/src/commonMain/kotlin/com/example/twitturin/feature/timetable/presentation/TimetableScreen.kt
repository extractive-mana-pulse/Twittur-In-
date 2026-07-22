package com.example.twitturin.feature.timetable.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.twitturin.core.designsystem.component.EmptyState
import com.example.twitturin.core.designsystem.component.ConfirmDialog
import com.example.twitturin.core.designsystem.component.LoadingBox
import com.example.twitturin.core.designsystem.component.LocalBottomBarVisibility
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.designsystem.component.SecondaryButton
import com.example.twitturin.core.designsystem.component.TwitturTopBarMore
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Ink
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.designsystem.theme.SurfaceMuted
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.presentation.ObserveAsEvents
import com.example.twitturin.core.presentation.UiText
import com.example.twitturin.feature.timetable.domain.TimetableRange
import com.example.twitturin.feature.timetable.presentation.grid.TimetableGrid
import com.example.twitturin.feature.timetable.presentation.grid.TimetableMonthCalendar
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimetableRoot(
    onOpenSubjectPicker: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TimetableViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingMessage by remember { mutableStateOf<UiText?>(null) }

    val pickDocument = rememberTimetableDocumentPicker(onPicked = { bytes, fileName ->
        viewModel.onAction(TimetableAction.OnFilePicked(bytes, fileName))
    })

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TimetableEvent.OpenSubjectPicker -> onOpenSubjectPicker()
            is TimetableEvent.ShowMessage -> pendingMessage = event.message
        }
    }

    pendingMessage?.let { message ->
        val text = message.asString()
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(text)
            pendingMessage = null
        }
    }

    TimetableScreen(
        state = state,
        onAction = viewModel::onAction,
        onUpload = { pickDocument?.invoke() },
        canUpload = pickDocument != null,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    state: TimetableState,
    onAction: (TimetableAction) -> Unit,
    onUpload: () -> Unit,
    canUpload: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    var menuExpanded by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    val bottomBarController = LocalBottomBarVisibility.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // "Landscape mode" per the design ask: a wide, short viewport (a rotated phone). Detected by
        // aspect ratio rather than a bare height threshold on purpose — collapsing the chrome changes
        // the available height, and a height threshold could feed that back and oscillate the mode.
        val compact = maxWidth > maxHeight && maxHeight < 600.dp

        // In compact mode the chrome (top bar, range toggle, nav row, bottom bar) hides while the
        // user scrolls the timetable down and returns the moment they scroll up — an immersive
        // reveal that hands the whole short screen to the grid. In portrait it stays pinned.
        var revealedByScroll by remember { mutableStateOf(true) }
        LaunchedEffect(compact) { revealedByScroll = true } // rotating back to portrait restores it
        val chromeVisible = !compact || revealedByScroll

        // Mirror onto the shared bottom bar, and always restore it when leaving this tab so another
        // tab never inherits a hidden bar.
        LaunchedEffect(chromeVisible) { bottomBarController.isVisible = chromeVisible }
        DisposableEffect(Unit) { onDispose { bottomBarController.isVisible = true } }

        val thresholdPx = with(LocalDensity.current) { ChromeToggleThreshold.toPx() }
        val chromeScrollConnection = remember(thresholdPx) {
            ChromeScrollConnection(thresholdPx) { visible -> revealedByScroll = visible }
        }

        val chromeEnter = expandVertically(expandFrom = Alignment.Top) + fadeIn()
        val chromeExit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(visible = chromeVisible, enter = chromeEnter, exit = chromeExit) {
                    TwitturTopBarMore(
                        title = strings.timetable,
                        menuExpanded = menuExpanded,
                        onMenuClick = { menuExpanded = true },
                        onMenuDismiss = { menuExpanded = false },
                        extraActions = {
                            BetaPill(modifier = Modifier.padding(end = 4.dp))
                            if (state.hasTimetable) {
                                BadgedBox(
                                    badge = {
                                        if (state.followedCount > 0) {
                                            Badge { Text(state.followedCount.toString()) }
                                        }
                                    },
                                ) {
                                    IconButton(onClick = { onAction(TimetableAction.OnOpenSubjectPicker) }) {
                                        Icon(
                                            TwitturIcons.Bookmark,
                                            contentDescription = strings.timetableSubjectsCta,
                                            tint = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        },
                        menu = {
                            // Always offer at least one item — an overflow menu that opens onto
                            // nothing reads as broken.
                            if (state.hasTimetable) {
                                DropdownMenuItem(text = { Text(strings.timetableReplace) }, onClick = { menuExpanded = false; onUpload() })
                                DropdownMenuItem(text = { Text(strings.timetableClear) }, onClick = { menuExpanded = false; showClearConfirm = true })
                            } else {
                                DropdownMenuItem(
                                    text = { Text(strings.timetableUploadCta) },
                                    onClick = { menuExpanded = false; onUpload() },
                                    enabled = canUpload,
                                )
                            }
                        },
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when {
                    state.isLoading -> LoadingBox()

                    !state.hasTimetable -> UploadEmptyState(onUpload = onUpload, canUpload = canUpload, errorMessage = state.error)

                    else -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            // Observe the grid/calendar's scroll to drive the reveal — only in
                            // compact mode, so portrait keeps the chrome permanently pinned.
                            .then(if (compact) Modifier.nestedScroll(chromeScrollConnection) else Modifier),
                    ) {
                        AnimatedVisibility(visible = chromeVisible, enter = chromeEnter, exit = chromeExit) {
                            Column {
                                RangeToggle(
                                    selected = state.visibleRange,
                                    isMonthUnlocked = state.isMonthUnlocked,
                                    onSelect = { onAction(TimetableAction.OnRangeSelect(it)) },
                                    compact = compact,
                                )
                                WindowNavRow(state = state, onAction = onAction, compact = compact)
                            }
                        }

                        // The grid/calendar takes the remaining space (weight) so its own internal
                        // scroll is bounded and it never overflows past the bottom of the screen.
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            when {
                                state.followedCount == 0 -> NoSubjectsFollowedState(
                                    onPickSubjects = { onAction(TimetableAction.OnOpenSubjectPicker) },
                                )

                                state.visibleRange == TimetableRange.MONTH -> {
                                    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
                                    state.anchorDate?.let { anchor ->
                                        TimetableMonthCalendar(
                                            monthAnchor = anchor,
                                            dots = state.monthDots,
                                            today = today,
                                            onDayClick = { date -> onAction(TimetableAction.OnJumpToDate(date)) },
                                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                        )
                                    }
                                }

                                else -> TimetableGrid(
                                    windowDates = state.windowDates,
                                    blocksByDate = state.blocksByDate,
                                    onLessonClick = { onAction(TimetableAction.OnLessonClick(it)) },
                                )
                            }
                        }
                    }
                }
            }
        }

        state.selectedLesson?.let { lesson ->
            LessonDetailSheet(lesson = lesson, onDismiss = { onAction(TimetableAction.OnDismissLessonDetail) })
        }

        if (showClearConfirm) {
            ConfirmDialog(
                title = strings.timetableClear,
                message = strings.timetableClearConfirmMessage,
                confirmLabel = strings.delete,
                destructive = true,
                onConfirm = { showClearConfirm = false; onAction(TimetableAction.OnClearTimetable) },
                onDismiss = { showClearConfirm = false },
            )
        }
    }
}

/** Scroll distance in one direction before the chrome flips visibility — small enough to feel
 *  responsive, large enough not to trigger on incidental jitter. */
private val ChromeToggleThreshold = 12.dp

/**
 * Flips the timetable chrome's visibility from the direction of a nested scroll: scrolling the
 * content up (finger moving up, [available].y negative) past the threshold hides it; scrolling back
 * down reveals it. Reading the gesture here rather than from a hoisted `LazyListState` means it
 * works uniformly for the Day/Week grid (a `verticalScroll`) and the Month view (a `LazyVerticalGrid`)
 * without either having to expose its internal scroll state.
 */
private class ChromeScrollConnection(
    private val thresholdPx: Float,
    private val onRevealChange: (Boolean) -> Unit,
) : NestedScrollConnection {
    private var accumulated = 0f

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val dy = available.y
        if (dy != 0f) {
            // Reset the run whenever the scroll direction flips, so a hide/reveal needs a fresh,
            // deliberate drag rather than leftover travel from the opposite direction.
            if ((dy < 0f) != (accumulated < 0f)) accumulated = 0f
            accumulated += dy
            when {
                accumulated <= -thresholdPx -> onRevealChange(false)
                accumulated >= thresholdPx -> onRevealChange(true)
            }
        }
        return Offset.Zero
    }
}

@Composable
private fun BetaPill(modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    Text(
        text = strings.timetableBetaBadge.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Brand,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceMuted)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun UploadEmptyState(onUpload: () -> Unit, canUpload: Boolean, errorMessage: UiText?, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    EmptyState(
        icon = TwitturIcons.Bookmark,
        title = strings.timetableUploadCta,
        subtitle = strings.timetableUploadHint,
        modifier = modifier,
        action = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                errorMessage?.let { message ->
                    Text(
                        text = message.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
                if (canUpload) {
                    PrimaryButton(
                        text = if (errorMessage != null) strings.timetableParseErrorRetry else strings.timetableUploadCta,
                        onClick = onUpload,
                        modifier = Modifier.widthIn(max = CtaMaxWidth),
                    )
                }
            }
        },
    )
}

@Composable
private fun NoSubjectsFollowedState(onPickSubjects: () -> Unit, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    EmptyState(
        icon = TwitturIcons.Bookmark,
        title = strings.timetableNoSubjectsTitle,
        subtitle = strings.timetableNoSubjectsSubtitle,
        modifier = modifier,
        action = {
            SecondaryButton(
                text = strings.timetablePickSubjects,
                onClick = onPickSubjects,
                modifier = Modifier.widthIn(max = CtaMaxWidth),
            )
        },
    )
}

/** The design-system CTA buttons fill their width; cap them so they don't stretch edge-to-edge
 *  on a desktop window. */
private val CtaMaxWidth = 320.dp

@Composable
private fun RangeToggle(
    selected: TimetableRange,
    isMonthUnlocked: Boolean,
    onSelect: (TimetableRange) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val strings = LocalStrings.current
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = if (compact) 4.dp else 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            RangeChip(strings.timetableRangeDay, TimetableRange.DAY, selected, true, onSelect, Modifier.weight(1f))
            RangeChip(strings.timetableRangeThreeDay, TimetableRange.THREE_DAY, selected, true, onSelect, Modifier.weight(1f))
            RangeChip(strings.timetableRangeWeek, TimetableRange.WEEK, selected, true, onSelect, Modifier.weight(1f))
            RangeChip(strings.timetableRangeMonth, TimetableRange.MONTH, selected, isMonthUnlocked, onSelect, Modifier.weight(1f))
        }
        // The month-locked hint is the first thing to drop when height is scarce.
        if (!isMonthUnlocked && !compact) {
            Text(
                text = strings.timetableMonthLockedHint,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun RangeChip(
    label: String,
    range: TimetableRange,
    selected: TimetableRange,
    enabled: Boolean,
    onSelect: (TimetableRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = range == selected
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Brand else SurfaceMuted)
            .let { if (enabled) it.clickable { onSelect(range) } else it }
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) OnBrand else Ink,
        )
    }
}

@Composable
private fun WindowNavRow(
    state: TimetableState,
    onAction: (TimetableAction) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val strings = LocalStrings.current
    val iconButtonModifier = if (compact) Modifier.size(36.dp) else Modifier
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = if (compact) 0.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onAction(TimetableAction.OnPrevWindow) }, modifier = iconButtonModifier) {
            Icon(TwitturIcons.Back, contentDescription = null, tint = SecondaryText)
        }
        Text(
            text = windowLabel(state),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Ink,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
        )
        Text(
            text = strings.timetableToday,
            style = MaterialTheme.typography.labelMedium,
            color = Brand,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onAction(TimetableAction.OnToday) }.padding(8.dp),
        )
        IconButton(onClick = { onAction(TimetableAction.OnNextWindow) }, modifier = iconButtonModifier) {
            Icon(TwitturIcons.Back, contentDescription = null, tint = SecondaryText, modifier = Modifier.rotate(180f))
        }
    }
}

private val MonthShortNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private fun windowLabel(state: TimetableState): String {
    if (state.visibleRange == TimetableRange.MONTH) {
        val anchor = state.anchorDate ?: return ""
        return "${MonthShortNames[anchor.month.ordinal]} ${anchor.year}"
    }
    val dates = state.windowDates
    if (dates.isEmpty()) return ""
    val first = dates.first()
    val last = dates.last()
    val firstLabel = "${MonthShortNames[first.month.ordinal]} ${first.day}"
    if (dates.size == 1) return firstLabel
    val lastLabel = if (last.month == first.month) {
        "${last.day}"
    } else {
        "${MonthShortNames[last.month.ordinal]} ${last.day}"
    }
    return "$firstLabel – $lastLabel"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonDetailSheet(lesson: TimetableBlockUi, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(lesson.subjectName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(16.dp))
            DetailRow(label = strings.timetableTimeLabel, value = "${lesson.startLabel} – ${lesson.endLabel}")
            if (lesson.teacherNames.isNotEmpty()) DetailRow(label = strings.timetableTeacherLabel, value = lesson.teacherNames)
            if (lesson.roomNames.isNotEmpty()) DetailRow(label = strings.timetableRoomLabel, value = lesson.roomNames)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = Ink)
    }
}
