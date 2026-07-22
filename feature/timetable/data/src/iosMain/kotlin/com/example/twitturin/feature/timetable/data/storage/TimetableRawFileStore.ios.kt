package com.example.twitturin.feature.timetable.data.storage

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDataWritingAtomic
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL

/**
 * Foundation file I/O via `NSData`/`NSURL`. Every call here was checked against this exact
 * toolchain's actual bridged API — via `klib dump-metadata` on the resolved Foundation klib for
 * ios_simulator_arm64, not assumed from a (possibly older-SDK) example — since this SDK's
 * `NSData.writeToURL`/`dataWithContentsOfURL` don't have the simple 1-2 arg forms some online
 * examples use; only the explicit `options`/`error` overloads resolve here.
 */
@OptIn(ExperimentalForeignApi::class)
actual class TimetableRawFileStore {

    private val fileManager = NSFileManager.defaultManager

    private val fileUrl by lazy {
        fileManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )?.URLByAppendingPathComponent(TIMETABLE_SNAPSHOT_FILE_NAME)
    }

    actual suspend fun write(json: String) {
        val url = fileUrl ?: return
        val bytes = json.encodeToByteArray()
        val data = if (bytes.isEmpty()) {
            NSData()
        } else {
            bytes.usePinned { pinned -> NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong()) }
        }
        data.writeToURL(url, options = NSDataWritingAtomic, error = null)
    }

    actual suspend fun read(): String? {
        val url = fileUrl ?: return null
        val data = NSData.dataWithContentsOfURL(url, options = 0uL, error = null) ?: return null
        val bytes = data.bytes?.readBytes(data.length.toInt()) ?: return null
        return bytes.decodeToString()
    }

    actual suspend fun clear() {
        val url = fileUrl ?: return
        fileManager.removeItemAtURL(url, error = null)
    }
}
