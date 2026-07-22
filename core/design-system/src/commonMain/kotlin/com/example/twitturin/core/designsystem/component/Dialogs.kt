package com.example.twitturin.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.Danger
import com.example.twitturin.core.designsystem.theme.Hint
import com.example.twitturin.core.designsystem.theme.SecondaryText

/**
 * Themed confirm dialog — 22dp corners, title 20/700, body in secondary, text actions
 * (dismiss in secondary, confirm in brand or danger when [destructive]).
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissLabel: String = "No",
    destructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.background,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(message, style = MaterialTheme.typography.bodyLarge, color = SecondaryText) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = if (destructive) Danger else Brand, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissLabel, color = SecondaryText, style = MaterialTheme.typography.labelLarge)
            }
        },
    )
}

/**
 * Destructive confirmation gated behind typed input: the confirm action stays disabled until the
 * user types [expectedText] exactly (e.g. their username before deleting the account).
 */
@Composable
fun TypedConfirmDialog(
    title: String,
    message: String,
    expectedText: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissLabel: String = "Cancel",
    placeholder: String = "",
) {
    var typed by remember { mutableStateOf("") }
    val matches = typed.trim() == expectedText
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = MaterialTheme.colorScheme.background,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                Text(message, style = MaterialTheme.typography.bodyLarge, color = SecondaryText)
                Spacer(Modifier.height(6.dp))
                Text("@$expectedText", style = MaterialTheme.typography.bodyLarge, color = Danger)
                Spacer(Modifier.height(12.dp))
                BrandTextField(
                    value = typed,
                    onValueChange = { typed = it },
                    placeholder = placeholder,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = matches) {
                Text(confirmLabel, color = if (matches) Danger else Hint, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissLabel, color = SecondaryText, style = MaterialTheme.typography.labelLarge)
            }
        },
    )
}
