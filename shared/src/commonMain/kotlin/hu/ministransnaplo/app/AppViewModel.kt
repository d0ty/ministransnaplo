/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppState(val loggedIn: Boolean = false) {
}

class AppViewModel: ViewModel() {
    val appState: StateFlow<AppState>
        field = MutableStateFlow<AppState>(AppState())

    val supabase = createSupabaseClient(Secrets.supa_url, Secrets.supa_key) {
        install(Postgrest)
        install(Auth)
        install(Functions)
        install(Storage)
        install(Coil3Integration)
    }

    fun login() {
        viewModelScope.launch {
            kotlin.runCatching {
                supabase.auth.signInWith(Email) {
                    email = "test@doty.hu"
                    password = "test"
                }
            }.onSuccess { appState.update { it.copy(loggedIn = true) } }
                .onFailure {it.printStackTrace() }
        }
    }
}