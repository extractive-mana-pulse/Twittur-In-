package com.example.twitturin.feature.timetable.presentation

import androidx.compose.runtime.Composable
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

/** Desktop: the AWT file dialog filtered to `.xml`. */
@Composable
actual fun rememberTimetableDocumentPicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)? {
    return {
        val dialog = FileDialog(null as Frame?, "Choose timetable XML", FileDialog.LOAD)
        dialog.setFilenameFilter { _, name -> name.endsWith(".xml", ignoreCase = true) }
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
