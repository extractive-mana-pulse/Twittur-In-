package com.example.twitturin.feedback.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.twitturin.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSend: (subject: String, message: String) -> Unit
) {
    val topics = stringArrayResource(R.array.feedback_array).toList()

    var expanded by remember { mutableStateOf(false) }
    var selectedTopic by rememberSaveable { mutableStateOf(topics.firstOrNull().orEmpty()) }
    var customTitle by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    val isOther = selectedTopic == "Other"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.feedback)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.feedback_page_back_button)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = selectedTopic,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.major)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    topics.forEach { topic ->
                        DropdownMenuItem(
                            text = { Text(text = topic) },
                            onClick = {
                                selectedTopic = topic
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Shown only for the "Other" topic; its text becomes the email subject.
            if (isOther) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = customTitle,
                    onValueChange = { customTitle = it },
                    singleLine = true,
                    label = { Text(text = "Please describe title of your report…") }
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = message,
                onValueChange = { message = it },
                minLines = 4,
                label = { Text(text = "Please describe your report…") }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    val subject = if (isOther && customTitle.isNotBlank()) customTitle else selectedTopic
                    onSend(subject, message.trim())
                }
            ) {
                Text(text = stringResource(R.string.set_off))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedbackScreenPreview() {
    FeedbackScreen(onBack = {}, onSend = { _, _ -> })
}
