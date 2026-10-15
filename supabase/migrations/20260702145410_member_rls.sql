/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

alter table "public"."guard"
    add column if not exists "owner" uuid not null default gen_random_uuid();
CREATE UNIQUE INDEX IF not exists guard_owner_key ON public.guard USING btree (owner);
alter table "public"."guard"
    add constraint "guard_owner_key" UNIQUE using index "guard_owner_key";

grant select, insert, update, delete on table "public"."guard" to "anon";
grant select, insert, update, delete on table "public"."guard" to "authenticated";


grant select, insert, update, delete on table "public"."member" to "anon";
grant delete on table "public"."member" to "authenticated";
alter table "public"."member" enable row level security;

set
check_function_bodies = off;

CREATE
OR REPLACE FUNCTION public.get_my_guard()
 RETURNS public.guard
 LANGUAGE sql
 STABLE SECURITY DEFINER
 SET search_path TO 'public'
AS $function$
select g.*
from public."member" m
         join public."guard" g
              on g."id" = m."guard"
where m."login" = auth.uid() limit 1
$function$
;


create
policy if not exists "Allow guard owners to create new members for their guards"
  on "public"."member"
  as permissive
  for insert
  to authenticated
with check (((( SELECT auth.uid() AS uid) = ( SELECT get_my_guard.owner
   FROM public.get_my_guard() get_my_guard(id, name, owner))) AND (guard = ( SELECT get_my_guard.id
   FROM public.get_my_guard() get_my_guard(id, name, owner)))));

  create
policy if not exists "Allow guard owners to delete their members"
  on "public"."member"
  as permissive
  for delete
to authenticated
using (((guard = ( SELECT get_my_guard.id
   FROM public.get_my_guard() get_my_guard(id, name, owner))) AND (( SELECT auth.uid() AS uid) = ( SELECT get_my_guard.owner
   FROM public.get_my_guard() get_my_guard(id, name, owner)))));

  create
policy if not exists "Allow guard owners to edit their members"
  on "public"."member"
  as permissive
  for
update
    to authenticated
    using ((guard = ( SELECT get_my_guard.id
    FROM public.get_my_guard() get_my_guard(id, name, owner))))
with check ((( SELECT auth.uid() AS uid) = ( SELECT get_my_guard.owner
    FROM public.get_my_guard() get_my_guard(id, name, owner))));

create
policy if not exists "Allow logged in users to read their own guard members"
  on "public"."member"
  as permissive
  for
select
    to authenticated
    using (((login = ( SELECT auth.uid() AS uid)) OR (guard = ( SELECT get_my_guard.id
    FROM public.get_my_guard() get_my_guard(id, name)))));