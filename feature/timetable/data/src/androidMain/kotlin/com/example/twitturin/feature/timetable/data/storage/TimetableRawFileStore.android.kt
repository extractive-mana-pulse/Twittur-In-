package com.example.twitturin.feature.timetable.data.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class TimetableRawFileStore(private val context: Context) {

    private val file: File get() = File(context.filesDir, TIMETABLE_SNAPSHOT_FILE_NAME)

    actual suspend fun write(json: String) {
        withContext(Dispatchers.IO) { file.writeText(json) }
    }

    actual suspend fun read(): String? = withContext(Dispatchers.IO) {
        file.takeIf { it.exists() }?.readText()
    }

    actual suspend fun clear() {
        withContext(Dispatchers.IO) { file.delete() }
        Unit
    }
}
