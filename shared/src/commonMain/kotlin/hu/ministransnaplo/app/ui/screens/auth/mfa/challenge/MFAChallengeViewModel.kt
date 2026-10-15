/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.auth.mfa.challenge

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.MFAChallenge
import hu.ministransnaplo.app.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MFAChallengeViewModel(val authRepository: AuthRepository) : AppViewModel(authRepository) {

    val state: StateFlow<MFAChallenge>
        field = MutableStateFlow<MFAChallenge>(MFAChallenge())

    init {
        viewModelScope.launch {
            authRepository.awaitAuthInit()
            val challenge = authRepository.createChallenge()
            state.update { challenge }
        }
    }

    fun verifyChallenge(code: String, onVerificationFinishes: suspend (success: Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.verifyChallenge(state.value, code)
                .also { onVerificationFinishes(it) }
        }
    }
}