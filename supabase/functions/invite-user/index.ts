/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

import "@supabase/functions-js/edge-runtime.d.ts";
import { withSupabase } from "@supabase/server";
import {
    check_is_guard_owner,
    cors_headers,
    db_error_resp,
    no_content_success,
    permission_error_resp,
} from "../_shared/index.ts";

export default {
    fetch: withSupabase(
        { auth: ["user"], cors: cors_headers },
        async (req, ctx) => {
            const guard_result = await check_is_guard_owner(ctx);
            if (guard_result.error !== null) return guard_result.error;

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

            if (guard_result.guard_id !== member_guard_id) {
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

            return no_content_success;
        },
    ),
};
