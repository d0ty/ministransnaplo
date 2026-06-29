/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */


package hu.ministransnaplo.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform