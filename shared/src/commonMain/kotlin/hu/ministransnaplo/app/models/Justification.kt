/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

sealed interface Justification {
    val justificationId: String?

    fun getColor(): Color = if (justificationId == null) Color.White else Color.Green

    @Serializable
    data class WithMember(
        @SerialName("id")
        val memberId: String,
        @SerialName("name")
        val memberName: String,
        @SerialName("justification_id")
        override val justificationId: String?
    ) : Justification

    @Serializable
    data class WithProgram(
        @SerialName("id")
        val programId: String,
        val title: String,
        val start: Instant,
        @SerialName("justification_id")
        override val justificationId: String?,
    ) : Justification
}