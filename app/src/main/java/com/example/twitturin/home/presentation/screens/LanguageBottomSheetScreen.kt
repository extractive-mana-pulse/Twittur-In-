package com.example.twitturin.home.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.twitturin.R

private data class LanguageOption(val label: String, val code: String)

@Composable
fun LanguageBottomSheetScreen(
    modifier: Modifier = Modifier,
    onSave: (String) -> Unit
) {
    val options = listOf(
        LanguageOption("English", "en"),
        LanguageOption("Italiano", "it"),
        LanguageOption("Русский", "ru"),
        LanguageOption("O'zbek", "uz")
    )
    var selected by rememberSaveable { mutableStateOf("en") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Language", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        options.forEach { option ->
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selected = option.code }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == option.code,
                    onClick = { selected = option.code }
                )
                Text(text = option.label, modifier = Modifier.padding(start = 8.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSave(selected) }
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}
