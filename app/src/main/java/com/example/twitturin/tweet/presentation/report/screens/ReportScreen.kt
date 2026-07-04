package com.example.twitturin.tweet.presentation.report.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.twitturin.R

private data class ReportOption(val subject: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSubmit: (subject: String, context: String) -> Unit
) {
    val options = listOf(
        ReportOption(stringResource(R.string.spam), stringResource(R.string.scam_desc)),
        ReportOption(stringResource(R.string.privacy), stringResource(R.string.privacy_desc)),
        ReportOption(stringResource(R.string.abuse_and_harassment), stringResource(R.string.abuse_and_harassment_desc)),
        ReportOption(stringResource(R.string.other), "")
    )
    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    var otherText by rememberSaveable { mutableStateOf("") }
    val isOther = selectedIndex == options.lastIndex

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.report)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.report_expl_desc),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedIndex = index }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = option.subject, style = MaterialTheme.typography.bodyLarge)
                        if (option.description.isNotEmpty()) {
                            Text(text = option.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (isOther) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    value = otherText,
                    onValueChange = { otherText = it },
                    label = { Text(text = stringResource(R.string.other)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedIndex >= 0,
                onClick = {
                    val option = options[selectedIndex]
                    val context = if (isOther) otherText else option.description
                    onSubmit(option.subject, context)
                }
            ) {
                Text(text = stringResource(R.string.next))
            }
        }
    }
}
