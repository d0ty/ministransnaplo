/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

create table justification
(
    id      uuid primary key default gen_random_uuid(),
    program uuid references programs on delete cascade not null,
    member  uuid references member on delete cascade   not null,
    guard   uuid references guard on delete cascade    not null
);

alter table "public"."justification"
    enable row level security;

grant select, insert, delete on justification to authenticated;
grant select, insert, update, delete on member to service_role;

create
    policy "Allow guard users to create new justification for their guards"
    on "public"."justification"
    as permissive
    for insert
    to authenticated
    with check (guard = (SELECT get_my_guard.id
                         FROM public.get_my_guard() get_my_guard(id, name, owner)));

create
    policy "Allow guard users to delete justifications for their guards"
    on "public"."justification"
    as permissive
    for delete
    to authenticated
    using (guard = (SELECT get_my_guard.id
                    FROM public.get_my_guard() get_my_guard(id, name, owner)));

create
    policy "Allow guard users to read justifications of their guards"
    on "public"."justification"
    as permissive
    for select
    to authenticated
    using (guard = (SELECT get_my_guard.id
                    FROM public.get_my_guard() get_my_guard(id, name, owner)));
