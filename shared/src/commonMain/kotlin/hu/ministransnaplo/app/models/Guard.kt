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
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.coil.coil3
import io.github.jan.supabase.storage.authenticatedStorageItem
import kotlinx.serialization.Serializable

@Serializable
class Guard(
    val id: String,
    val name: String,
) {
    @OptIn(SupabaseExperimental::class)
    @Composable
    fun ProfilePicture(supabase: SupabaseClient, size: Dp = 100.dp) {
        val imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
            .components {
                add(supabase.coil3)
                add(KtorNetworkFetcherFactory())
            }.build()
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(size).clip(RoundedCornerShape(16.dp)).background(color = Color.White).padding(4.dp)
            ) {
                AsyncImage(
                    model = authenticatedStorageItem("guard-profile", "$id.png"),
                    imageLoader = imageLoader,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
