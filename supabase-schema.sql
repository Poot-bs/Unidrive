-- Run this script in Supabase SQL editor before starting the app in supabase mode.

create table if not exists public.users_store (
  id text primary key,
  email text unique not null,
  payload text not null,
  updated_at timestamptz not null default now()
);

create table if not exists public.trajets_store (
  id text primary key,
  payload text not null,
  updated_at timestamptz not null default now()
);

create table if not exists public.reservations_store (
  id text primary key,
  trajet_id text not null,
  passager_id text not null,
  payload text not null,
  updated_at timestamptz not null default now()
);

create index if not exists idx_reservations_store_trajet_id on public.reservations_store(trajet_id);
create index if not exists idx_reservations_store_passager_id on public.reservations_store(passager_id);

create or replace function public.touch_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

-- Recreate triggers safely
DROP TRIGGER IF EXISTS trg_users_store_touch_updated_at ON public.users_store;
DROP TRIGGER IF EXISTS trg_trajets_store_touch_updated_at ON public.trajets_store;
DROP TRIGGER IF EXISTS trg_reservations_store_touch_updated_at ON public.reservations_store;

create trigger trg_users_store_touch_updated_at
before update on public.users_store
for each row execute function public.touch_updated_at();

create trigger trg_trajets_store_touch_updated_at
before update on public.trajets_store
for each row execute function public.touch_updated_at();

create trigger trg_reservations_store_touch_updated_at
before update on public.reservations_store
for each row execute function public.touch_updated_at();
