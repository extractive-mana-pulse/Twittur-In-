@file:Suppress("MISSING_DEPENDENCY_CLASS_IN_EXPRESSION_TYPE")

package com.example.twitturin.notification.presentation.screens

import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.twitturin.R
import com.example.twitturin.notification.domain.model.MainGit
import com.example.twitturin.notification.presentation.vm.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel
) {
    val gitData by viewModel.gitData.collectAsStateWithLifecycle()
    val isLoadingState = viewModel.isLoading.collectAsStateWithLifecycle()
    val isLoading: Boolean = isLoadingState.value
    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.notifications),
                        )
                    }
                )
            }
        ) { innerPadding ->
            Log.d("NotificationScreen", "isLoading: $isLoading")
            if (isLoading)
                LoadingScreen(innerPadding)
            else
                LoadedScreen(innerPadding, gitData)
        }
    }
}

@Composable
fun LoadedScreen(
    innerPadding: PaddingValues,
    gitData: MainGit?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (gitData != null) {
            Text(
                text = gitData.name ?: "No patch",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.Start)
            )

            Text(
                text = "Version: ${gitData.assetsUrl}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .align(Alignment.Start)
            )

            Text(
                text = "Published: ${gitData.url}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Start)
            )

            HorizontalDivider()

        } else {
            LoadingScreen(
                innerPadding
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    innerPadding : PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedPreloader(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(282.dp),
            rawResId = R.raw.empty_notifications_list,
        )
        Text(
            text = stringResource(R.string.notification_title),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
                platformStyle = MaterialTheme.typography.bodyMedium.platformStyle,
                textAlign = MaterialTheme.typography.bodyMedium.textAlign,
                lineHeightStyle = MaterialTheme.typography.bodyMedium.lineHeightStyle,
                textGeometricTransform = MaterialTheme.typography.bodyMedium.textGeometricTransform,
                textDecoration = MaterialTheme.typography.bodyMedium.textDecoration,
            ),
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = stringResource(R.string.notification_message),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight,
                letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing,
                platformStyle = MaterialTheme.typography.bodySmall.platformStyle,
                textAlign = MaterialTheme.typography.bodySmall.textAlign,
                lineHeightStyle = MaterialTheme.typography.bodySmall.lineHeightStyle,
                textGeometricTransform = MaterialTheme.typography.bodySmall.textGeometricTransform,
                textDecoration = MaterialTheme.typography.bodySmall.textDecoration,
            )
        )
    }
}

@Composable
fun AnimatedPreloader(
    modifier: Modifier = Modifier,
    @RawRes rawResId: Int
) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(rawResId)
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = { preloaderProgress },
        modifier = modifier
    )
}