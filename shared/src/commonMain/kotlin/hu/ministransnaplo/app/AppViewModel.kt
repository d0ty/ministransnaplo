/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import androidx.lifecycle.ViewModel
import hu.ministransnaplo.app.models.AuthState
import hu.ministransnaplo.app.repositories.AuthRepository
import kotlinx.coroutines.flow.StateFlow

open class AppViewModel(val authRepo: AuthRepository) : ViewModel() {
    val userState: StateFlow<AuthState> = authRepo.authState

    suspend fun singOut() = authRepo.singOut()
}