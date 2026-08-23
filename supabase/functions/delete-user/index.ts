/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

// Follow this setup guide to integrate the Deno language server with your editor:
// https://deno.land/manual/getting_started/setup_your_environment
// This enables autocomplete, go to definition, etc.

// Setup type definitions for built-in Supabase Runtime APIs
import "@supabase/functions-js/edge-runtime.d.ts";
import { withSupabase } from "@supabase/server";
import {
    cors_headers,
    db_error_resp,
    no_content_success,
} from "../_shared/index.ts";

export default {
    fetch: withSupabase(
        { auth: ["user"], cors: cors_headers },
        async (req, ctx) => {
            const { member_login } = await req.json();

            const { error: userDeleteError } = ctx.supabaseAdmin.auth.admin
                .deleteUser(member_login);
            if (userDeleteError) {
                return db_error_resp;
            }

            return no_content_success;
        },
    ),
};

/* To invoke locally:

  1. Run `supabase start` (see: https://supabase.com/docs/reference/cli/supabase-start)
  2. Make an HTTP request:

  curl -i --location --request POST 'http://127.0.0.1:54321/functions/v1/delete-user' \
    --header 'apiKey: sb_publishable_ACJWlzQHlZjBrEguHvfOxg_3BJgxAaH' \
    --data '{"name":"Functions"}'

*/
