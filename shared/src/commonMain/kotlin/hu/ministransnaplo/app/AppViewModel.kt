/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found in the LICENSE file.
 */

package hu.ministransnaplo.app

import androidx.lifecycle.ViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

open class AppViewModel : ViewModel() {
    val supabase = createSupabaseClient(Secrets.supa_url, Secrets.supa_key) {
        install(Postgrest)
        install(Auth)
        install(Functions)
        install(Storage)
        install(Coil3Integration)
    }
}