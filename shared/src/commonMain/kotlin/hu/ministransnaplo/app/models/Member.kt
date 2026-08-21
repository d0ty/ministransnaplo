/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val id: String = "",
    val name: String,
    @SerialName("islecturer")
    val isLecturer: Boolean,
    val role: Role = Role.NORMAL,
    @SerialName("login")
    val userId: String? = null,
    val email: String? = null,
) {
    val isLeader: Boolean
        get() = userId != null

    fun computeRank(isLecturer: Boolean) = "${if (isLeader) "Vezető" else "Tag"}${if (isLecturer) ", lektor" else ""}"

    val rank: String
        get() = computeRank(isLecturer)

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
}
