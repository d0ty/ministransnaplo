create table if not exists guard
(
    id
    uuid
    primary
    key
    default
    gen_random_uuid
(
),
    name text not null
    );

grant select on guard to authenticated;
grant select, insert, update, delete on guard to service_role;

create
policy "Allow authenticated read access" on guard for
select using (true);
alter table guard enable row level security;

insert into guard (name)
values ('Teszt gárda');