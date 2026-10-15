/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

create or replace function public.get_justifications_for_program("programId" text)
    returns table
            (
                id               uuid,
                guard            uuid,
                name             text,
                justification_id uuid
            )
    language sql
    stable
as
$$
select m.id,
       m.guard,
       m.name,
       j.id as justification_id
from public.member as m
         left join public.justification as j
                   on j.member = m.id
                       and j.program = "programId"::uuid;
$$;
