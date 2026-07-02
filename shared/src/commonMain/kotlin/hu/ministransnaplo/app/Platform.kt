/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */


package hu.ministransnaplo.app

interface Platform {
    val name: String
    val fullScreenDialogs: Boolean
}

expect fun getPlatform(): Platform