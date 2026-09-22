/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

create table programs
(
    id          uuid primary key                  default gen_random_uuid(),
    title       text                     not null,
    description text                              default null,
    start       timestamp with time zone not null,
    duration    interval                 not null,
    mandatory   boolean                  not null default false,
    guard       uuid references guard on delete cascade
);

grant select, insert on programs to authenticated;
grant update (title, description, start, duration, mandatory) on programs to authenticated;
grant select, insert, update, delete on member to service_role;

create
    policy "Allow guard owners to create new programs for their guards"
    on "public"."programs"
    as permissive
    for insert
    to authenticated
    with check ((((SELECT auth.uid() AS uid) = (SELECT get_my_guard.owner
                                                FROM public.get_my_guard() get_my_guard(id, name, owner))) AND
                 (guard = (SELECT get_my_guard.id
                           FROM public.get_my_guard() get_my_guard(id, name, owner)))));

create
    policy "Allow guard owners to delete their porgrams"
    on "public"."programs"
    as permissive
    for delete
    to authenticated
    using (((guard = (SELECT get_my_guard.id
                      FROM public.get_my_guard() get_my_guard(id, name, owner))) AND
            ((SELECT auth.uid() AS uid) = (SELECT get_my_guard.owner
                                           FROM public.get_my_guard() get_my_guard(id, name, owner)))));

create
    policy "Allow guard owners to edit their programs"
    on "public"."programs"
    as permissive
    for
    update
    to authenticated
    using ((guard = (SELECT get_my_guard.id
                     FROM public.get_my_guard() get_my_guard(id, name, owner))))
    with check (((SELECT auth.uid() AS uid) = (SELECT get_my_guard.owner
                                               FROM public.get_my_guard() get_my_guard(id, name, owner))));

create
    policy "Allow logged in users to read their own guard programs"
    on "public"."programs"
    as permissive
    for
    select
    to authenticated
    using (guard = (SELECT get_my_guard.id
                    FROM public.get_my_guard() get_my_guard(id, name)));
