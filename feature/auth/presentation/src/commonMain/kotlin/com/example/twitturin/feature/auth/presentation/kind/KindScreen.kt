package com.example.twitturin.feature.auth.presentation.kind

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.component.BrandTopBar
import com.example.twitturin.core.designsystem.component.SecondaryButton
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.presentation.LocalStrings
import com.example.twitturin.core.designsystem.theme.SecondaryText

/** Account-kind picker. Pure stateless screen — navigation is delegated to callbacks. */
@Composable
fun KindScreen(
    onBack: () -> Unit,
    onProfessor: () -> Unit,
    onStudent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { BrandTopBar(title = LocalStrings.current.signUp, onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(72.dp))
            Text(
                text = LocalStrings.current.whoAreYou,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Pick how you'll use TwitturIn",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(44.dp))
            SecondaryButton(text = LocalStrings.current.student, onClick = onStudent, leadingIcon = TwitturIcons.Account)
            Spacer(modifier = Modifier.height(16.dp))
            SecondaryButton(text = LocalStrings.current.professor, onClick = onProfessor, leadingIcon = TwitturIcons.Feedback)
        }
    }
}
