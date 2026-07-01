/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.auth.mfa.challenge

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Guard
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MFAChallengeViewModel : AppViewModel() {
    data class State(
        val factorId: String = "",
        val challengeId: String = "",
        val displayName: String = "",
        val guard: Guard? = null
    )

    val state: StateFlow<State>
        field = MutableStateFlow<State>(State())

    init {
        viewModelScope.launch {
            supabase.auth.awaitInitialization()
            val factor = supabase.auth.mfa.retrieveFactorsForCurrentUser()[0]
            val challengeId = supabase.auth.mfa.createChallenge(factor.id).id
            val user = supabase.auth.currentUserOrNull()
            val guard = supabase.from("guard").select().decodeAs<List<Guard>>()[0] // TODO: fetch the user's guard
            state.update {
                State(
                    factorId = factor.id,
                    challengeId = challengeId,
                    displayName = user?.userMetadata?.get("display_name") as? String ?: user?.email ?: "Felhasználó",
                    guard = guard
                )
            }
        }
    }

    fun verifyChallenge(code: String, onVerificationFinishes: suspend (success: Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                supabase.auth.mfa.verifyChallenge(state.value.factorId, state.value.challengeId, code)
                onVerificationFinishes(true)
            } catch (e: Exception) {
                onVerificationFinishes(false)
            }
        }
    }
}