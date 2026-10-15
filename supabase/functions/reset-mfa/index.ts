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
} from "../_shared/index.ts";

export default {
    fetch: withSupabase(
        { auth: ["user"], cors: cors_headers },
        async (req, ctx) => {
            const guard_res = await check_is_guard_owner(ctx);
            if (guard_res.error !== null) return guard_res.error;

            const { user_id } = await req.json();

            const { data: factorData, error: mfaListError } = await ctx
                .supabaseAdmin
                .auth.admin.mfa
                .listFactors({ userId: user_id.trim() });
            if (mfaListError) {
                console.error(mfaListError);
                return db_error_resp;
            }

            for (const factor of factorData.factors) {
                const { error: factorDeleteError } = await ctx.supabaseAdmin
                    .auth
                    .admin
                    .mfa.deleteFactor({
                        id: factor.id,
                        userId: user_id,
                    });
                if (factorDeleteError) {
                    console.error(factorDeleteError);
                    return db_error_resp;
                }
            }

            return no_content_success;
        },
    ),
};
