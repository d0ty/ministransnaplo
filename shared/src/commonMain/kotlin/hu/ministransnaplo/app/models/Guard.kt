/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.models

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import hu.ministransnaplo.app.util.SupabaseImage
import io.github.jan.supabase.annotations.SupabaseExperimental
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Guard(
    val id: String,
    val name: String,
    @SerialName("owner")
    val ownerId: String
) {
    @OptIn(SupabaseExperimental::class)
    @Composable
    fun ProfilePicture(size: Dp = 100.dp) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(size).clip(RoundedCornerShape(16.dp)).background(color = Color.White).padding(4.dp)
            ) {
                AsyncImage(
                    model = SupabaseImage("guard-profile", "$id.png"),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
