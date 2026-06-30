/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app.ui.screens.auth

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.ui.NavItem
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.mfa.AuthenticatorAssuranceLevel
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import hu.ministransnaplo.app.ui.screens.auth.mfa.MFAEnroll as MFAEnrollDest

class LoginViewModel : AppViewModel() {
    enum class LoginResult(val navItem: NavItem? = null, val success: Boolean, val errorMessage: String?) {
        MFAEnroll(MFAEnrollDest, true, null),
        MFAChallenge(LoggedIn, true, null), //TODO: when mfa challenge gets implemented navigate there
        AuthError(null, false, "Hiba a belépéskor"),
        SystemError(null, false, "Hiba a beléptetés közben. Kérjük próbáld újra kicsit később!")
    }

    fun login(emailAddress: String, pass: String, onLoginFinished: (LoginResult) -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                supabase.auth.signInWith(Email) {
                    email = emailAddress
                    password = pass
                }
            }.onSuccess {
                supabase.auth.mfa.getAuthenticatorAssuranceLevel().let { assuranceLevel ->
                    when (assuranceLevel.current) {
                        AuthenticatorAssuranceLevel.AAL1 -> onLoginFinished(LoginResult.MFAEnroll)
                        AuthenticatorAssuranceLevel.AAL2 -> onLoginFinished(LoginResult.MFAChallenge)
                    }
                }
            }.onFailure { it ->
                when (it) {
                    is AuthRestException -> onLoginFinished(LoginResult.AuthError)
                    is HttpRequestTimeoutException -> onLoginFinished(LoginResult.SystemError)
                    else -> onLoginFinished(LoginResult.SystemError)
                }
            }
        }
    }

}