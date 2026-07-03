package com.example.twitturin.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.component.BrandTextField
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.PrimaryButton

private const val FEEDBACK_EMAIL = "invoker1441@gmail.com"

private val FEEDBACK_TOPICS = listOf(
    "Find a bug!",
    "Function does not work properly!",
    "I cannot finish navigation!",
    "I cannot delete my account!",
    "Other",
)

@Composable
fun FeedbackRoot(
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    FeedbackScreen(
        onBack = onBack,
        onSend = { subject, message ->
            uriHandler.openUri("mailto:$FEEDBACK_EMAIL?subject=${enc(subject)}&body=${enc(message)}")
            onBack()
        },
    )
}

@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    onSend: (subject: String, message: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var topic by rememberSaveable { mutableStateOf(FEEDBACK_TOPICS.first()) }
    var customTitle by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    val isOther = topic == "Other"
    val subject = if (isOther) customTitle.trim() else topic
    val canSend = message.isNotBlank() && (!isOther || customTitle.isNotBlank())

    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = "Feedback", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = topic,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(14.dp),
                    label = { Text("Topic") },
                    trailingIcon = { Text(if (expanded) "▲" else "▼") },
                )
                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    FEEDBACK_TOPICS.forEach { item ->
                        DropdownMenuItem(text = { Text(item) }, onClick = { topic = item; expanded = false })
                    }
                }
            }

            if (isOther) {
                BrandTextField(
                    value = customTitle,
                    onValueChange = { customTitle = it },
                    placeholder = "Describe the title of your report…",
                )
            }

            BrandTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = "Tell us what's going on…",
                singleLine = false,
            )

            PrimaryButton(
                text = "Send",
                onClick = { onSend(subject, message.trim()) },
                enabled = canSend,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** Minimal percent-encoding for mailto subject/body (commonMain has no URLEncoder). */
private fun enc(value: String): String = buildString {
    value.forEach { c ->
        when (c) {
            ' ' -> append("%20")
            '\n' -> append("%0A")
            '\r' -> {}
            '&' -> append("%26")
            '?' -> append("%3F")
            '#' -> append("%23")
            '%' -> append("%25")
            '=' -> append("%3D")
            else -> append(c)
        }
    }
}
