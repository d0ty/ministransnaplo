/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */


package hu.ministransnaplo.app

import web.navigator.navigator

class JsPlatform : Platform {
    private val userAgent = navigator.userAgent
    private val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")

    override val name: String = userAgent.findAnyOf(browserList, ignoreCase = true)
        ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
        ?: "Unknown"
}

actual fun getPlatform(): Platform = JsPlatform()