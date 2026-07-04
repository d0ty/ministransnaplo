/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.util

import io.github.jan.supabase.functions.Functions
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject

suspend fun Functions.invokeWithJsonBody(function: String, body: JsonObjectBuilder.() -> Unit): HttpResponse {
    return this.invoke(function = function, body = buildJsonObject(body), headers = Headers.build {
        append(HttpHeaders.ContentType, "application/json")
    })
}