package com.example.twitturin

import androidx.compose.ui.window.ComposeUIViewController
import com.example.twitturin.di.initKoin
import platform.UIKit.UIViewController

@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}
