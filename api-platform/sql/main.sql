SET default_storage_engine = Innodb;
create database if not exists api_platform
    charset utf8
    collate utf8_bin;

use api_platform;

create table if not exists user
(
    id       bigint      not null auto_increment,
    nickname varchar(32) not null,
    username varchar(32) not null,
    passwd   varchar(32) not null,
    email    varchar(64) not null default '',

    role     varchar(16) not null default 'user',
    status   tinyint     not null default 0,
    ctime    bigint      not null,
    deleted  tinyint(1)  not null default 0,
    primary key (id)
);


create table if not exists http_api_group
(
    id          int           not null auto_increment,
    user_id     bigint        not null,
    name        varchar(128)  not null,
    description varchar(1024) not null default '',

    status      tinyint       not null default 0,
    ctime       bigint        not null,
    utime       bigint        not null,
    deleted     tinyint(1)    not null default 0,
    primary key (id)
);
create table if not exists http_api
(
    id           bigint        not null auto_increment,
    group_id     int           not null,
    user_id      bigint        not null,
    name         varchar(128)  not null,
    url          varchar(1024) not null,
    method       varchar(8)    not null comment 'upper',
    params       varchar(2048) not null default '',
    headers      varchar(1024) not null default '',
    req_body     varchar(2048) not null default '',
    resp_headers varchar(1024) not null default '',
    resp_body    varchar(2048) not null default '',
    examples     varchar(4096) not null default '',

    status       tinyint       not null default 0,
    ctime        bigint        not null,
    utime        bigint        not null,
    deleted      tinyint(1)    not null default 0,
    primary key (id)
);



create table if not exists api_call
(
    id                    bigint  not null auto_increment,
    api_id                bigint  not null,
    user_id               bigint  not null,

    terminable_left_times int     not null default 0,
    end_time              bigint  not null default 0,
    dateless_left_times   int     not null default 0,

    call_times            int     not null default 0,
    fail_times            int     not null default 0,

    status                tinyint not null default 0,
    ctime                 bigint  not null,
    primary key (id)
);

create table if not exists product_package
(
    id            int            not null auto_increment,
    description   varchar(256)   not null default '',

    price         decimal(10, 2) not null default 0,
    amount        int            not null default 0,
    duration_secs int            not null default 0 comment '0: no limit time',
    ctime         bigint         not null,
    primary key (id)
);
create table if not exists product_order
(
    id              varchar(32)    not null,
    package_deal_id int,
    user_id         bigint         not null,
    coupon_id       varchar(32)    not null default '',
    payment_amount  decimal(10, 2) not null,

    status          tinyint        not null,
    ctime           bigint         not null,
    deleted         tinyint(1)     not null,
    primary key (id)
);
create table if not exists coupon
(
    id               varchar(32)    not null,
    satisfied_price  decimal(10, 2) not null comment '0: directly use',
    discounted_price decimal(10, 2) not null,
    end_time         bigint         not null,
    description      varchar(64)    not null default '',
    valid            tinyint(1)     not null default 1,
    ctime            bigint         not null,
    primary key (id)
);
create table if not exists login_logout_record
(
    id         bigint  not null auto_increment,
    ip         varchar(15)  not null,
    user_agent varchar(128) not null,
    ctime      bigint       not null,
    primary key (id)
);

