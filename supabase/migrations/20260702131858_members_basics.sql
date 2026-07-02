/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

create type member_role as enum ('normal', 'sysadmin');

create table member
(
    id         uuid primary key                             default gen_random_uuid(),
    guard      uuid references guard on delete cascade,
    name       text        not null,
    isLecturer boolean     not null                         default false,
    role       member_role not null                         default 'normal',
    login      uuid references auth.users on delete cascade default null
);

grant select, insert on member to authenticated;
grant update (name, isLecturer, role) on member to authenticated;
grant select, insert, update, delete on member to service_role;