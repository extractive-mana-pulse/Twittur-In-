@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.richtexteditor.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.richtexteditor.RichTextEditorAction
import com.example.richtexteditor.RichTextEditorController
import com.example.richtexteditor.RichTextEditorState
import com.example.richtexteditor.RichTextFonts
import com.example.richtexteditor.icons.RichTextIcons
import com.example.richtexteditor.rememberRichTextFonts
import com.example.richtexteditor.util.TextColor
import com.example.richtexteditor.util.TextFontFamily
import com.example.richtexteditor.util.TextSize

/**
 * Upstream's floating formatting toolbar (Material3-expressive [HorizontalFloatingToolbar] with
 * an expand/collapse FAB), adapted only where Android resources forced it: drawables →
 * [RichTextIcons], `R.font` → [rememberRichTextFonts].
 */
@Composable
fun RichTextToolbar(
    controller: RichTextEditorController,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true,
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    var moreMenuExpanded by remember { mutableStateOf(false) }
    val state = controller.state
    val onAction = controller::onAction
    val fonts = rememberRichTextFonts()

    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = expanded,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = if (expanded)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.primaryContainer
            ) {
                val chevronRotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "chevron_rotation"
                )
                Icon(
                    imageVector = RichTextIcons.ArrowDown,
                    contentDescription = if (expanded) "Collapse formatting" else "Expand formatting",
                    modifier = Modifier.rotate(chevronRotation),
                    tint = if (expanded)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) {
        ToolbarToggleButton(
            icon = RichTextIcons.FormatBold,
            checked = state.isCurrentlyBold,
            contentDescription = "Bold",
            onClick = { onAction(RichTextEditorAction.OnBoldClick) }
        )

        ToolbarToggleButton(
            icon = RichTextIcons.FormatItalic,
            checked = state.isCurrentlyItalic,
            contentDescription = "Italic",
            onClick = { onAction(RichTextEditorAction.OnItalicClick) }
        )

        ToolbarToggleButton(
            icon = RichTextIcons.FormatUnderlined,
            checked = state.isCurrentlyUnderline,
            contentDescription = "Underline",
            onClick = { onAction(RichTextEditorAction.OnUnderlineClick) }
        )

        ToolbarDivider()

        ColorPickerButton(state = state, onAction = onAction)

        ToolbarDivider()

        MoreOptionsButton(
            menuExpanded = moreMenuExpanded,
            state = state,
            fonts = fonts,
            onAction = onAction,
            onDismiss = { moreMenuExpanded = false },
            onClick = { moreMenuExpanded = true }
        )
    }
}

@Composable
private fun MoreOptionsButton(
    menuExpanded: Boolean,
    state: RichTextEditorState,
    fonts: RichTextFonts,
    onAction: (RichTextEditorAction) -> Unit,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = RichTextIcons.MoreVert,
                contentDescription = "More formatting options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .align(Alignment.BottomCenter),
            offset = DpOffset(x = 40.dp, y = 0.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 6.dp,
            shape = CircleShape,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FontFamilyChip(state = state, fonts = fonts, onAction = onAction)
                FontSizeChip(state = state, onAction = onAction)

                ToolbarDivider()

                IconButton(onClick = {
                    onAction(RichTextEditorAction.OnResetClick)
                    onDismiss()
                }) {
                    Icon(
                        imageVector = RichTextIcons.Replay,
                        contentDescription = "Reset formatting",
                        modifier = Modifier
                            .rotate(45f),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarToggleButton(
    icon: ImageVector,
    checked: Boolean,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        animationSpec = tween(150),
        label = "toggle_container"
    )
    val iconTint by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(150),
        label = "toggle_tint"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint
            )
        }
    }
}

@Composable
private fun ColorPickerButton(
    state: RichTextEditorState,
    onAction: (RichTextEditorAction) -> Unit,
) {
    // The Default swatch previews as the theme's own text colour (what "inherit" renders as).
    val inheritColor = MaterialTheme.colorScheme.onSurface

    Box {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (state.isSelectColorDropdownExpanded)
                        MaterialTheme.colorScheme.secondaryContainer
                    else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true)
                ) { onAction(RichTextEditorAction.OnColorClick) },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = state.currentColor.color ?: inheritColor,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
            )
        }

        DropdownMenu(
            expanded = state.isSelectColorDropdownExpanded,
            onDismissRequest = { onAction(RichTextEditorAction.OnColorDropdownDismiss) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 6.dp,
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextColor.entries.forEach { option ->
                    val isSelected = option == state.currentColor
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                        label = "color_scale"
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .scale(scale)
                            .background(
                                color = option.color ?: inheritColor,
                                shape = CircleShape
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .clickable { onAction(RichTextEditorAction.OnColorChange(option)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FontFamilyChip(
    state: RichTextEditorState,
    fonts: RichTextFonts,
    onAction: (RichTextEditorAction) -> Unit,
) {
    Box {
        FilterChip(
            selected = state.isSelectFontFamilyDropdownExpanded,
            onClick = { onAction(RichTextEditorAction.OnFontFamilyClick) },
            label = {
                Text(
                    text = state.currentFontFamily.label(),
                    fontSize = 14.sp,
                    fontFamily = fonts.familyFor(state.currentFontFamily),
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (state.isSelectFontFamilyDropdownExpanded)
                        RichTextIcons.ArrowUp
                    else
                        RichTextIcons.ArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = state.isSelectFontFamilyDropdownExpanded,
                borderColor = MaterialTheme.colorScheme.outlineVariant,
                selectedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                borderWidth = 1.dp,
                selectedBorderWidth = 0.dp
            )
        )

        DropdownMenu(
            expanded = state.isSelectFontFamilyDropdownExpanded,
            onDismissRequest = { onAction(RichTextEditorAction.OnFontFamilyDropdownDismiss) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 6.dp,
            shape = RoundedCornerShape(16.dp),
        ) {
            TextFontFamily.entries.forEach { option ->
                val isSelected = option == state.currentFontFamily
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.displayName(),
                            fontFamily = fonts.familyFor(option),
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    },
                    onClick = { onAction(RichTextEditorAction.OnFontFamilyChange(option)) },
                    modifier = Modifier.background(
                        if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        else Color.Transparent
                    ),
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = RichTextIcons.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FontSizeChip(
    state: RichTextEditorState,
    onAction: (RichTextEditorAction) -> Unit,
) {
    Box {
        FilterChip(
            selected = state.isSelectFontSizeDropdownExpanded,
            onClick = { onAction(RichTextEditorAction.OnFontSizeClick) },
            label = {
                Text(
                    text = state.currentFontSize.label(),
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = RichTextIcons.TextFormat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (state.isSelectFontSizeDropdownExpanded)
                        RichTextIcons.ArrowUp
                    else
                        RichTextIcons.ArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = state.isSelectFontSizeDropdownExpanded,
                borderColor = MaterialTheme.colorScheme.outlineVariant,
                selectedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                borderWidth = 1.dp,
                selectedBorderWidth = 0.dp
            )
        )

        DropdownMenu(
            expanded = state.isSelectFontSizeDropdownExpanded,
            onDismissRequest = { onAction(RichTextEditorAction.OnFontSizeDropdownDismiss) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 6.dp,
            shape = RoundedCornerShape(16.dp),
        ) {
            TextSize.entries.forEach { option ->
                val isSelected = option == state.currentFontSize
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label(),
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    },
                    onClick = { onAction(RichTextEditorAction.OnFontSizeChange(option)) },
                    modifier = Modifier.background(
                        if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        else Color.Transparent
                    ),
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = RichTextIcons.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                )
            }
        }
    }
}
