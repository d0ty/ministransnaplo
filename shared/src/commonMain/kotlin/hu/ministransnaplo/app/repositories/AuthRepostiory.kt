/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.repositories

import hu.ministransnaplo.app.models.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.mfa.AuthenticatorAssuranceLevel
import io.github.jan.supabase.auth.mfa.FactorType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun signIn(email: String, password: String)
    suspend fun singOut()
    suspend fun awaitAuthInit()
    suspend fun createChallenge(): MFAChallenge
    suspend fun verifyChallenge(challenge: MFAChallenge, code: String): Boolean
    suspend fun unenrollFactors()
    suspend fun enrollFactor(): MFAEnrollment
}

class SupabaseAuthRepository(val supabase: SupabaseClient, scope: CoroutineScope) : AuthRepository {
    override val authState: StateFlow<AuthState>
        field = MutableStateFlow<AuthState>(AuthState.Loading)

    init {
        scope.launch {
            supabase.auth.sessionStatus.collect { status ->
                val state = when (status) {
                    is SessionStatus.Initializing -> AuthState.Loading
                    is SessionStatus.Authenticated -> {
                        val guard = supabase.postgrest.rpc("get_my_guard").decodeAs<Guard>()
                        AuthState.Authenticated(
                            User(
                                mfaState = MFAState.fromMFALevel(supabase.auth.mfa.getAuthenticatorAssuranceLevel()),
                                profile = status.session.user?.id?.let { value ->
                                    supabase.from("member").select { filter { eq("login", value) } }
                                        .decodeSingle<Member?>()
                                },
                                email = status.session.user?.email ?: "email@address",
                                userId = status.session.user?.id ?: "userId",
                                guard = guard
                            )
                        )
                    }

                    else -> AuthState.Unauthenticated
                }
                authState.update { state }
            }
        }
    }

    override suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun singOut() {
        supabase.auth.signOut()
    }

    override suspend fun awaitAuthInit() {
        supabase.auth.awaitInitialization()
    }

    override suspend fun createChallenge(): MFAChallenge {
        val factor = supabase.auth.mfa.retrieveFactorsForCurrentUser()[0]
        val challengeId = supabase.auth.mfa.createChallenge(factor.id).id
        return MFAChallenge(
            factorId = factor.id,
            challengeId = challengeId
        )
    }

    override suspend fun verifyChallenge(challenge: MFAChallenge, code: String): Boolean {
        return try {
            supabase.auth.mfa.verifyChallenge(challenge.factorId, challenge.challengeId, code)
            supabase.auth.mfa.getAuthenticatorAssuranceLevel().current == AuthenticatorAssuranceLevel.AAL2
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun unenrollFactors() {
        supabase.auth.mfa.retrieveFactorsForCurrentUser().forEach {
            supabase.auth.mfa.unenroll(it.id)
        }
    }

    override suspend fun enrollFactor(): MFAEnrollment {
        val factor = supabase.auth.mfa.enroll(FactorType.TOTP)
        return createChallenge().toEnrollment(factor.data.qrCode, factor.data.secret)
    }

}