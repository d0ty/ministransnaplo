/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

import {cors_headers} from "./cors.ts";

export const permission_error_resp = Response.json({
    message: "You have no permission to do this!",
}, { status: 401, headers: cors_headers });
export const db_error_resp = Response.json({
    message: "Database error occurred!",
}, {
    status: 502,
    headers: cors_headers,
});

export const no_content_success = new Response(null, {
    status: 204,
    headers: cors_headers,
});
