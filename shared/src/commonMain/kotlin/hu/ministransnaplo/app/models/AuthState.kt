/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.models

import io.github.jan.supabase.auth.mfa.MfaLevel
import io.github.jan.supabase.auth.mfa.AuthenticatorAssuranceLevel as AAL

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


data class User(
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

sealed interface AuthState {
    val userOrNull: User?
        get() = when (this) {
            is Authenticated -> user
            else -> null
        }

    data object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    data object Unauthenticated : AuthState
}

data class MFAChallenge(
    val factorId: String = "",
    val challengeId: String = "",
) {
    fun toEnrollment(qrCode: String, secret: String): MFAEnrollment =
        MFAEnrollment(factorId, qrCode, secret, challengeId)
}

data class MFAEnrollment(
    val factorId: String = "",
    val qrCode: String = "",
    val secret: String = "",
    val challengeId: String = "",
) {
    fun toChallenge(): MFAChallenge = MFAChallenge(factorId, challengeId)
}