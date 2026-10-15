/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.ClipboardItem

@OptIn(ExperimentalComposeUiApi::class, ExperimentalWasmJsInterop::class)
actual fun String.toClipEntry(): ClipEntry {
    return ClipEntry(createClipboardItemWithPlainText(this))
}

@OptIn(ExperimentalComposeUiApi::class)
@JsName("createClipboardItemWithPlainText")
fun createClipboardItemWithPlainText(text: String): JsArray<ClipboardItem> =
    js("[new ClipboardItem({'text/plain': new Blob([text], { type: 'text/plain' })})]")