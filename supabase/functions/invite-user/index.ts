/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

import "@supabase/functions-js/edge-runtime.d.ts";
import {withSupabase} from "@supabase/server";
import {cors_headers} from "../_shared/index.ts";

const permission_error_resp = Response.json({
    message: "You have no permission to do this!",
}, { status: 401, headers: cors_headers });
const db_error_resp = Response.json({ message: "Database error occurred!" }, {
    status: 502,
    headers: cors_headers,
});
export default {
    fetch: withSupabase(
        { auth: ["user"], cors: cors_headers },
        async (req, ctx) => {
            const { data: { id: guard_id, owner }, error: guard_fetch_error } =
                await ctx.supabase
                    .rpc("get_my_guard");
            if (guard_fetch_error) {
                console.error(guard_fetch_error);
                return db_error_resp;
            }
            if (ctx.userClaims?.id !== owner) {
                return permission_error_resp;
            }

            const { member_id, email } = await req.json();

            const {
                data: { guard: member_guard_id },
                error: member_check_error,
            } = await ctx
                .supabase.from("member").select()
                .eq("id", member_id).maybeSingle();

            if (member_check_error) {
                console.error(member_check_error);
                return db_error_resp;
            }

            if (guard_id !== member_guard_id) {
                return permission_error_resp;
            }

            const { data: { user: { id: user_id } }, error: invite_error } =
                await ctx
                    .supabaseAdmin.auth.admin.inviteUserByEmail(email, {
                        redirect_url: "http://localhost:8080",
                    });

            if (invite_error) {
                console.error(invite_error);
                return db_error_resp;
            }

            const { error: member_link_error } = await ctx.supabaseAdmin.from(
                "member",
            )
                .update({ login: user_id }).eq("id", member_id);

            if (member_link_error) {
                console.error(member_link_error);
                return db_error_resp;
            }

            return new Response(null, { status: 204, headers: cors_headers });
        },
    ),
};
