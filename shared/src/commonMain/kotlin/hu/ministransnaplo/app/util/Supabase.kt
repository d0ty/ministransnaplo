/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

package hu.ministransnaplo.app.util

import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject

suspend fun Functions.invokeWithJsonBody(function: String, body: JsonObjectBuilder.() -> Unit): HttpResponse {
    return this.invoke(function = function, body = buildJsonObject(body), headers = Headers.build {
        append(HttpHeaders.ContentType, "application/json")
    })
}

suspend fun executeSupabaseAction(action: suspend () -> DbResult): DbResult {
    try {
        return action()
    } catch (e: PostgrestRestException) {
        if (e.code != "42501") e.printStackTrace()
        return if (e.code == "42501") DbResult.Failure.PermissionDenied else DbResult.Failure.Error
    } catch (e: RestException) {
        return if (e.statusCode == 401) DbResult.Failure.PermissionDenied else DbResult.Failure.Error
    } catch (e: Exception) {
        e.printStackTrace()
        return DbResult.Failure.Error
    }
}