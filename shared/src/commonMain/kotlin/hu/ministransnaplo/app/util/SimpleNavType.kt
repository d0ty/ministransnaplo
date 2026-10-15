/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.util

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Simple [NavType] wrapper with JSON serialization.
 *
 * @param T must have [Serializable] annotation and a [KSerializer] available.
 * @param serializer the [KSerializer] implementation usually provided by `Class.serializer()` call.
 */
class SimpleNavType<T>(val serializer: KSerializer<T>) :
    NavType<T>(true) {
    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.write { putString(key, serializeAsValue(value)) }
    }

    override fun get(bundle: SavedState, key: String): T? =
        bundle.read { getStringOrNull(key) }?.let { parseValue(it) }

    override fun parseValue(value: String): T = Json.decodeFromString(serializer, value)

    override fun serializeAsValue(value: T): String {
        return Json.encodeToString(serializer, value)
    }
}