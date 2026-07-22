package com.example.twitturin.feature.timetable.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** System document picker (`GetContent`, unfiltered like the image picker — many file
 *  providers mis-tag `.xml` uploads, so the extension is checked after picking instead of
 *  relying on a MIME filter). Bytes are read off the main thread. */
@Composable
actual fun rememberTimetableDocumentPicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)? {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val bytes = runCatching {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull() ?: return@launch
                val name = uri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.contains('.') } ?: "timetable.xml"
                launch(Dispatchers.Main) { onPicked(bytes, name) }
            }
        }
    }
    return { launcher.launch("*/*") }
}
