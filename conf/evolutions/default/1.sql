# --- First database schema

# --- !Ups

create table employee (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_employee primary key (id))
;

create sequence employee_seq start with 1;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists employee;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists employee_seq;

