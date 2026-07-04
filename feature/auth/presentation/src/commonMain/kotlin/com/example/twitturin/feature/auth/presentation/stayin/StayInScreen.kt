package com.example.twitturin.feature.auth.presentation.stayin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.twitturin.core.designsystem.component.PrimaryButton
import com.example.twitturin.core.designsystem.component.SecondaryButton
import com.example.twitturin.core.designsystem.icon.TwitturIcons
import com.example.twitturin.core.designsystem.theme.Brand
import com.example.twitturin.core.designsystem.theme.BrandSoft
import com.example.twitturin.core.designsystem.theme.SecondaryText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StayInRoot(
    onFinished: () -> Unit,
    viewModel: StayInViewModel = koinViewModel(),
) {
    StayInScreen(
        onSave = {
            viewModel.setRemembered(true)
            onFinished()
        },
        onNotNow = {
            viewModel.setRemembered(false)
            onFinished()
        },
    )
}

@Composable
fun StayInScreen(
    onSave: () -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(CircleShape).background(BrandSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(TwitturIcons.Lock, contentDescription = null, tint = Brand, modifier = Modifier.size(44.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Save your login details?",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Stay signed in so you don't have to log in again next time.",
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryText,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = "Save", onClick = onSave)
        Spacer(modifier = Modifier.height(12.dp))
        SecondaryButton(text = "Not now", onClick = onNotNow)
    }
}
