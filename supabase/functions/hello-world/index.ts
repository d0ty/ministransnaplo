/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

// Follow this setup guide to integrate the Deno language server with your editor:
// https://deno.land/manual/getting_started/setup_your_environment
// This enables autocomplete, go to definition, etc.

// Setup type definitions for built-in Supabase Runtime APIs
import "@supabase/functions-js/edge-runtime.d.ts";

import {withSupabase} from "@supabase/server";

console.log("Hello from Functions!");

export default {
    fetch: withSupabase({
        auth: ["user"],
        cors: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":
                "authorization, x-client-info, apikey, content-type, x-retry-count, x-region",
            "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
        },
    }, async (req, ctx) => {
        const { message } = await req.json();
        const email = ctx.userClaims?.email || "User";
        return Response.json({
            message: `Hello ${email}! Your message is ${message}`,
        });
    }),
};
