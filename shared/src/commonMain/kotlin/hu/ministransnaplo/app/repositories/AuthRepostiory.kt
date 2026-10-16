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
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
    @OptIn(ExperimentalCoroutinesApi::class)
    override val authState: StateFlow<AuthState> = supabase.auth.sessionStatus
        //.onEach { println("AUTH 1 raw=$it") }
        .distinctUntilChanged { old, new ->
            when {
                old is SessionStatus.Authenticated && new is SessionStatus.Authenticated ->
                    old.session.user?.id == new.session.user?.id

                else -> old::class == new::class
            }
        }
        .mapLatest { status ->
            //println("AUTH 2 mapping=$status")
            when (status) {
                is SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    if (user == null) AuthState.Unauthenticated
                    else AuthState.Authenticated(loadUser(user))
                }

                else -> AuthState.Unauthenticated
            }
        }
        //.onEach { println("AUTH 3 mapped=$it") }
        //.catch { println("AUTH ERROR $it"); emit(AuthState.Unauthenticated) }
        .stateIn(scope, SharingStarted.Eagerly, AuthState.Loading)

    private suspend fun loadUser(user: UserInfo): User {
        val guard = supabase.postgrest.rpc("get_my_guard").decodeAs<Guard>()
        return User(
            mfaState = MFAState.fromMFALevel(supabase.auth.mfa.getAuthenticatorAssuranceLevel()),
            profile = supabase.from("member").select { filter { eq("login", user.id) } }
                .decodeSingle<Member?>(),
            email = user.email ?: "email@address",
            userId = user.id,
            guard = guard
        )
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