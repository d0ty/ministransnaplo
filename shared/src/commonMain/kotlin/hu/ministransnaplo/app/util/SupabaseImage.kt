/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.util

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.Options
import coil3.request.crossfade
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import okio.Buffer

data class SupabaseImage(
    val bucket: String,
    val path: String
)

class SupabaseStorageFetcher(
    private val client: SupabaseClient,
    private val data: SupabaseImage,
    private val options: Options
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        val bytes = client.storage.from(data.bucket).downloadAuthenticated(data.path)
        return SourceFetchResult(
            source = ImageSource(Buffer().apply { write(bytes) }, options.fileSystem),
            mimeType = null,
            dataSource = DataSource.NETWORK,
        )
    }

    class Factory(private val client: SupabaseClient) : Fetcher.Factory<SupabaseImage> {
        override fun create(
            data: SupabaseImage,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return SupabaseStorageFetcher(client, data, options)
        }
    }
}

class AppImageLoaderFactory(
    private val storageFetcher: SupabaseStorageFetcher.Factory,
) : SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(storageFetcher)
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true).build()
    }
}