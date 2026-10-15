/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

drop view member_data;
create view member_data as
select m.id,
       m.guard,
       m.name,
       m.islecturer,
       m.role,
       m.login,
       count(j.id)                                          as justificationCount,
       (select count(*) from public.programs) - count(j.id) as omissionCount
from public.member as m
         left join public.justification as j
                   on j.member = m.id
group by m.id;