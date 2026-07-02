/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.ui.screens.members

import androidx.lifecycle.viewModelScope
import hu.ministransnaplo.app.AppViewModel
import hu.ministransnaplo.app.models.Member
import hu.ministransnaplo.app.util.DbResult
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MembersViewModel : AppViewModel() {
    fun createMember(name: String, isLecturer: Boolean, email: String, onResult: (DbResult) -> Unit) {
        if (email.isNotBlank()) {
            // TODO: user creation using supabase edge functions
            throw NotImplementedError()
        }
        // Normal member creation
        viewModelScope.launch(Dispatchers.Default) {
            try {
                supabase.from("member")
                    .insert(Member.New(guard = userState.value.guard!!.id, name = name, isLecturer = isLecturer))
                onResult(DbResult.Success.NoContent)
            } catch (e: PostgrestRestException) {
                onResult(if (e.code == "42501") DbResult.Failure.PermissionDenied else DbResult.Failure.Error)
                if (e.code != "42501") e.printStackTrace()
            } catch (e: Exception) {
                onResult(DbResult.Failure.Error)
                e.printStackTrace()
            }
        }
    }
}