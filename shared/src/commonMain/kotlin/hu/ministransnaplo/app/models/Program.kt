/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Instant

@Serializable
data class Program(
    val id: String = "",
    val title: String,
    val description: String,
    @SerialName("start")
    val startDate: Instant,
    val duration: String,
    val mandatory: Boolean,
) {
    @Transient
    val color = if (mandatory) Color(0x456DF95B) else Color(0xFF44CD31)

    @Serializable
    data class New(
        val guard: String,
        val title: String,
        val description: String,
        @SerialName("start")
        val startDate: Instant,
        val duration: String,
        val mandatory: Boolean,
    )
}