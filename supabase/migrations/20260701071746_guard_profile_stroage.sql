/*
 * Copyright 2026 doty and László Rab
 * Use of this source code is governed by the GNU General Public License that can be found at the LICENSE file
 */

insert into storage.buckets (id, name, public)
values ('guard-profile', 'guard-profile', false);

create
policy "Authenticated users can view assets in the guard-profile bucket"
on storage.objects
for
select
    to authenticated
    using (
    bucket_id = 'guard-profile'
    );