/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

import {permission_error_resp} from "./responses.ts";

export type GuardCheckResult = {
    guard_id: string;
    error: Response | null;
};

export const check_is_guard_owner = async (ctx): Promise<GuardCheckResult> => {
    const { data: { id: guard_id, owner }, error: guard_fetch_error } =
        await ctx.supabase
            .rpc("get_my_guard");
    if (guard_fetch_error) {
        console.error(guard_fetch_error);
        return { guard_id: "", error: guard_fetch_error };
    }
    if (ctx.userClaims?.id !== owner) {
        return { guard_id, error: permission_error_resp };
    }
    return { guard_id, error: null };
};
