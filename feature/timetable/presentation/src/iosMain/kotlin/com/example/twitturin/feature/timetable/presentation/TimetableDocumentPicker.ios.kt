package com.example.twitturin.feature.timetable.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject

/**
 * `UIDocumentPickerViewController` in its `documentTypes`/`inMode` + single-URL delegate shape
 * (not the newer `forOpeningContentTypes:` / `UTType` API). "public.xml" is the standard system
 * UTI for XML documents. The `NSData` read call was checked against this toolchain's actual
 * bridged API (`klib dump-metadata`) — see `TimetableRawFileStore.ios.kt`'s doc for why the
 * explicit `options`/`error` overload is used rather than a simpler-looking one-arg form.
 *
 * The profile feature's image picker never implemented an iOS actual at all (returns null
 * there); this is genuinely new code and, as the highest-risk piece of this feature, is the
 * first thing worth a real device/simulator pass before shipping.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberTimetableDocumentPicker(onPicked: (bytes: ByteArray, fileName: String) -> Unit): (() -> Unit)? {
    val delegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentAtURL: NSURL) {
                didPickDocumentAtURL.startAccessingSecurityScopedResource()
                val data = NSData.dataWithContentsOfURL(didPickDocumentAtURL, options = 0uL, error = null)
                didPickDocumentAtURL.stopAccessingSecurityScopedResource()
                controller.dismissViewControllerAnimated(true, null)

                val bytes = data?.bytes?.readBytes(data.length.toInt())
                val name = didPickDocumentAtURL.lastPathComponent ?: "timetable.xml"
                if (bytes != null) onPicked(bytes, name)
            }
        }
    }
    return {
        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("public.xml"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen,
        )
        picker.setDelegate(delegate)
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, true, null)
    }
}
