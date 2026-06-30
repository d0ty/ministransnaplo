/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.util

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun String.toClipEntry(): ClipEntry = ClipEntry(ClipData.newPlainText(null, this))