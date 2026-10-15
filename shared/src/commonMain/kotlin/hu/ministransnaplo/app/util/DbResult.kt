/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.util

sealed class DbResult(val success: Boolean) {
    sealed class Success : DbResult(true) {
        data class WithContent<T>(val result: T) : Success()
        data object NoContent : Success()
    }

    sealed class Failure(val description: String) : DbResult(false) {
        data object PermissionDenied : Failure("Nincs jogosultságod a művelet végrehajtáshoz.")
        data object Error : Failure("Hiba. Kérjük próbáld újra később!")
    }
}