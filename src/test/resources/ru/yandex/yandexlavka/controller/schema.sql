drop table if exists courier cascade;

create table courier
(
    courier_id   bigserial
        primary key,
    courier_type varchar(255)
);


drop table if exists courier_regions;

create table courier_regions
(
    courier_courier_id bigint not null
            references courier,
    regions            integer
);

drop table if exists courier_working_hours;

create table courier_working_hours
(
    courier_courier_id bigint not null
            references courier,
    "end"              time,
    start              time
);