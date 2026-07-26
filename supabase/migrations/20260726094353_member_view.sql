/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

create view member_data as
select member.id,
       member.guard,
       member.name,
       member.islecturer,
       member.role,
       profile.email
from public.member as member
         left join auth.users as profile on member.login = profile.id;