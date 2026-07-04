package com.example.twitturin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.twitturin.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Twittur",
        ) {
            App()
        }
    }
}
