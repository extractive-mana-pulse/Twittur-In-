package com.example.twitturin.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/** Desktop has no native share sheet — copy the text to the system clipboard instead. */
@Composable
actual fun rememberShareHandler(): (String) -> Unit = remember {
    { text ->
        val selection = StringSelection(text)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
    }
}
