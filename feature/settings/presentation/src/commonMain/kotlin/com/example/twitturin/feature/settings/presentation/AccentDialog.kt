package com.example.twitturin.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.OnBrand
import com.example.twitturin.core.designsystem.theme.SecondaryText
import com.example.twitturin.core.domain.preferences.AppAccent

/** Accent-colour picker — a swatch per [AppAccent], current one checked. */
@Composable
fun AccentDialog(
    current: AppAccent,
    onSelect: (AppAccent) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.background,
        title = { Text("Accent colour", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                AppAccent.entries.forEach { accent ->
                    val selected = accent == current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(accent); onDismiss() }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(26.dp).clip(CircleShape).background(Color(accent.argb)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (selected) {
                                Icon(TwitturIcons.Check, contentDescription = null, tint = OnBrand, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = accent.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selected) Color(accent.argb) else SecondaryText,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done", style = MaterialTheme.typography.labelLarge) }
        },
    )
}
