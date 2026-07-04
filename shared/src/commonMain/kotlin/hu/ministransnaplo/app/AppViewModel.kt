/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.models.Guard
import hu.ministransnaplo.app.models.Member
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.mfa.MfaLevel
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.mfa.AuthenticatorAssuranceLevel as AAL

open class AppViewModel : ViewModel() {
    enum class MFAState {
        ENROLL_REQUIRED,
        CHALLENGE_REQUIRED,
        VERIFIED;

        companion object {
            fun fromMFALevel(mfaLevel: MfaLevel): MFAState {
                return when (mfaLevel.current) {
                    AAL.AAL1 if mfaLevel.next == AAL.AAL1 -> ENROLL_REQUIRED
                    AAL.AAL1 if mfaLevel.next == AAL.AAL2 -> CHALLENGE_REQUIRED
                    AAL.AAL2 -> VERIFIED
                    else -> {
                        throw IllegalArgumentException("Unknown MFA state: $mfaLevel")
                    }
                }
            }
        }
    }

    data class UserState(
        val loggedIn: Boolean = false,
        val mfaState: MFAState? = null,
        val userId: String = "",
        val email: String = "email@address",
        val guard: Guard? = null,
        val profile: Member? = null,
    ) {
        val displayName: String = profile?.name ?: ""
        val isGuardOwner: Boolean
            get() = if (guard == null) false else guard.ownerId == userId
    }

    val userState: StateFlow<UserState>
        field = MutableStateFlow(UserState())


    @OptIn(SupabaseExperimental::class)
    val supabase = createSupabaseClient(Secrets.supa_url, Secrets.supa_key) {
        install(Postgrest)
        install(Auth)
        install(Functions)
        install(Storage)
        install(Coil3Integration)
    }.also { supa ->
        viewModelScope.launch {
            supa.auth.sessionStatus.collect { event ->
                if (event !is SessionStatus.Authenticated && event !is SessionStatus.NotAuthenticated) return@collect;
                if (event is SessionStatus.NotAuthenticated) {
                    if (event.isSignOut) userState.update { UserState() }
                    return@collect
                }
                val authEvent = event as SessionStatus.Authenticated
                val guard = supa.postgrest.rpc("get_my_guard").decodeAs<Guard>()
                userState.update {
                    UserState(
                        loggedIn = true,
                        mfaState = MFAState.fromMFALevel(supa.auth.mfa.getAuthenticatorAssuranceLevel()),
                        profile = authEvent.session.user?.id?.let { value ->
                            supa.from("member").select { filter { eq("login", value) } }.decodeSingle<Member?>()
                        },
                        email = authEvent.session.user?.email ?: "email@address",
                        userId = authEvent.session.user?.id ?: "userId",
                        guard = guard
                    )
                }
            }
        }
    }
}