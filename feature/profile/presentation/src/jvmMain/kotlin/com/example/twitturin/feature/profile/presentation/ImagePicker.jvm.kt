package com.example.twitturin.feature.profile.presentation

import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

/** Desktop: the AWT file dialog filtered to common image extensions. */
@Composable
actual fun rememberImagePicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)? {
    return {
        val dialog = FileDialog(null as Frame?, "Choose image", FileDialog.LOAD)
        dialog.setFilenameFilter { _, name ->
            name.matches(Regex(".*\\.(png|jpe?g|webp|gif)$", RegexOption.IGNORE_CASE))
        }
        dialog.isVisible = true
        val dir = dialog.directory
        val file = dialog.file
        if (dir != null && file != null) {
            runCatching {
                val f = File(dir, file)
                onPicked(f.readBytes(), f.name)
            }
        }
    }
}
