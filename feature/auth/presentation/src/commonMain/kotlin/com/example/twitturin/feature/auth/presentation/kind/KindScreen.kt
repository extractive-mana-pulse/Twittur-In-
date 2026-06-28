package com.example.twitturin.feature.auth.presentation.kind

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Account-kind picker. Pure stateless screen — navigation is delegated to callbacks. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KindScreen(
    onBack: () -> Unit,
    onProfessor: () -> Unit,
    onStudent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Sign up") },
                navigationIcon = { TextButton(onClick = onBack) { Text(text = "Back") } },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "I am a...",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 96.dp),
            )

            Spacer(modifier = Modifier.height(48.dp))

            KindButton(text = "Professor", onClick = onProfessor)

            Spacer(modifier = Modifier.height(24.dp))

            KindButton(text = "Student", onClick = onStudent)
        }
    }
}

@Composable
private fun KindButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
        )
    }
}
