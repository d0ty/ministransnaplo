/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.models

import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Member(
    val id: String = "",
    val name: String,
    @SerialName("islecturer")
    val isLecturer: Boolean,
    val role: Role = Role.NORMAL,
    @SerialName("login")
    val userId: String? = null,
) {
    val isLeader: Boolean
        get() = userId != null

    val rank: String
        get() = "${if (isLeader) "Vezető" else "Tag"}${if (isLecturer) ", lektor" else ""}"

    @Serializable
    enum class Role {
        @SerialName("normal")
        NORMAL,

        @SerialName("sysadmin")
        SYSADMIN
    }

    @Serializable
    data class New(
        val guard: String,
        val name: String,
        @SerialName("islecturer")
        val isLecturer: Boolean,
        val role: Role = Role.NORMAL,
    )

    object NavType : androidx.navigation.NavType<Member>(isNullableAllowed = false) {
        override fun put(
            bundle: SavedState,
            key: String,
            value: Member
        ) {
            bundle.write { putString(key, serializeAsValue(value)) }
        }

        override fun get(
            bundle: SavedState,
            key: String
        ): Member? =
            bundle.read { getStringOrNull(key) }?.let { parseValue(it) }


        override fun parseValue(value: String): Member =
            Json.decodeFromString<Member>(value)

        override fun serializeAsValue(value: Member): String =
            Json.encodeToString(value)
    }
}
