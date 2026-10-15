/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.auth.mfa.enroll

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.MFAEnrollment
import hu.ministransnaplo.app.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MFAEnrollViewModel(val authRepository: AuthRepository) : AppViewModel(authRepository) {
    val state: StateFlow<MFAEnrollment?>
        field = MutableStateFlow<MFAEnrollment?>(null)

    init {
        viewModelScope.launch {
            authRepository.awaitAuthInit()
            authRepository.unenrollFactors()
            state.update { authRepository.enrollFactor() }
        }
    }

    fun verifyChallenge(code: String, onVerificationFinishes: suspend (success: Boolean) -> Unit) {
        viewModelScope.launch {
            onVerificationFinishes(authRepository.verifyChallenge(state.value!!.toChallenge(), code))
        }
    }
}