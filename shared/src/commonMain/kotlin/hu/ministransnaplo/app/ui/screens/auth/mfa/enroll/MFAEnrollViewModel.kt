/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens.auth.mfa.enroll

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.mfa.AuthenticatorAssuranceLevel
import io.github.jan.supabase.auth.mfa.FactorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MFAEnrollViewModel : AppViewModel() {
    data class State(
        val loading: Boolean = true,
        val factorId: String = "",
        val qrCode: String = "",
        val secret: String = "",
        val challengeId: String = ""
    )

    val state: StateFlow<State>
        field = MutableStateFlow<State>(State())

    init {
        viewModelScope.launch {
            supabase.auth.awaitInitialization()
            supabase.auth.mfa.retrieveFactorsForCurrentUser().forEach {
                supabase.auth.mfa.unenroll(it.id)
            }
            val factor = supabase.auth.mfa.enroll(FactorType.TOTP)
            val challengeId = supabase.auth.mfa.createChallenge(factor.id).id
            state.update {
                State(
                    false,
                    factorId = factor.id,
                    qrCode = factor.data.qrCode,
                    secret = factor.data.secret,
                    challengeId = challengeId
                )
            }
        }
    }

    fun verifyChallenge(code: String, onVerificationFinishes: suspend (success: Boolean) -> Unit) {
        viewModelScope.launch {
            supabase.auth.mfa.verifyChallenge(state.value.factorId, state.value.challengeId, code)
            onVerificationFinishes(supabase.auth.mfa.getAuthenticatorAssuranceLevel().current == AuthenticatorAssuranceLevel.AAL2)
        }
    }
}