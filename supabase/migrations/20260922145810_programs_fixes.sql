/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

alter table "public"."programs"
    enable row level security;
alter table "public"."programs"
    alter column "guard" set not null;