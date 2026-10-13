/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

create or replace function public.get_justifications_by_member("memberId" text)
    returns table
            (
                id               uuid,
                title            text,
                start            timestamptz,
                justification_id uuid
            )
    language sql
    stable
as
$$
select p.id,
       p.title,
       p.start,
       j.id as justification_id
from public.programs as p
         left join public.justification as j
                   on j.program = p.id
                       and j.member = "memberId"::uuid
where j.id is null;
$$;