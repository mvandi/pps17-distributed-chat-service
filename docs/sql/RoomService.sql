-- Database Section
-- ________________ 

use roomservice;

-- Tables Section
-- _____________ 

drop table if exists messages;
create table messages (
     username varchar(20) not null,
     `name` varchar(50) not null,
     `timestamp` timestamp not null default CURRENT_TIMESTAMP,
     content varchar(1024) not null,
     constraint id_message primary key (username, `name`, `timestamp`));

drop table if exists participations;
create table participations (
     username varchar(20) not null,
     `name` varchar(50) not null,
     join_date timestamp not null default CURRENT_TIMESTAMP,
     constraint id_participation primary key (username, `name`));

drop table if exists rooms;
create table rooms (
     `name` varchar(50) not null,
     owner_username varchar(20) not null,
     constraint id_room primary key (`name`));

drop table if exists users;
create table users (
     username varchar(20) not null,
     constraint id_user primary key (username));

-- Constraints Section
-- ___________________ 

-- alter table messages add constraint FKPM
--     foreign key (username, `name`)
--     references participations (username, `name`)
--     on delete cascade;

alter table participations add constraint FKPR
     foreign key (`name`)
     references rooms (`name`)
     on delete cascade;

alter table participations add constraint FKUP
     foreign key (username)
     references users (username)
     on delete cascade;

alter table rooms add constraint FKOR
     foreign key (owner_username)
     references users (username)
     on delete cascade;

-- Index Section
-- _____________ 
