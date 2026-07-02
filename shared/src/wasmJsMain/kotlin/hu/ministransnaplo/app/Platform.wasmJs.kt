/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

object WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val fullScreenDialogs: Boolean
        get() = false
}

actual fun getPlatform(): Platform = WasmPlatform