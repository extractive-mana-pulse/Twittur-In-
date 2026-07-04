package com.example.twitturin.auth.presentation.kind.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.twitturin.R

@Composable
fun KindScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onProfessor: () -> Unit,
    onStudent: () -> Unit
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }

            Text(
                text = stringResource(R.string.kind_picker_tv),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 96.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            KindButton(text = stringResource(R.string.professor), onClick = onProfessor)

            Spacer(modifier = Modifier.height(24.dp))

            KindButton(text = stringResource(R.string.student), onClick = onStudent)
        }
    }
}

@Composable
private fun KindButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun KindScreenPreview() {
    KindScreen(onBack = {}, onProfessor = {}, onStudent = {})
}
