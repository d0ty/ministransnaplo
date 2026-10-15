/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */


package hu.ministransnaplo.app

import android.os.Build

object AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val isMobile: Boolean
        get() = true
}

actual fun getPlatform(): Platform = AndroidPlatform