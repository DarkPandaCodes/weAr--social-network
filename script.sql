create table authorities
(
    authority varchar(255) null,
    username  varchar(255) not null
        primary key
)
    engine = MyISAM;

create table authority
(
    id        int auto_increment
        primary key,
    authority varchar(255) null,
    constraint UK_nrgoi6sdvipfsloa7ykxwlslf
        unique (authority)
)
    engine = MyISAM;

create table cities
(
    city_id    int auto_increment
        primary key,
    city       varchar(255) null,
    country_id int          null
)
    engine = MyISAM;

create index FK6gatmv9dwedve82icy8wrkdmk
    on cities (country_id);

create table comments_table
(
    comment_id int auto_increment
        primary key,
    content    varchar(255) null,
    likes      int          null,
    post_id    int          null,
    user_id    int          null
)
    engine = MyISAM;

create index FK2u9jv4nfg7r53p4jsa5r6du44
    on comments_table (post_id);

create index FK3ef9s87opud5wxtobcp24ej3n
    on comments_table (user_id);

create table connections_table
(
    connection_id     int auto_increment
        primary key,
    user_accepter_id  int null,
    user_requester_id int null
)
    engine = MyISAM;

create index FKoqnpe7indmmieldy8tddycsy1
    on connections_table (user_requester_id);

create table countries
(
    id      int auto_increment
        primary key,
    country varchar(255) null
)
    engine = MyISAM;

create table locations
(
    location_id int auto_increment
        primary key,
    city_id     int null
)
    engine = MyISAM;

create index FKgvg10vlf7gqvbgf9rbbho3gyg
    on locations (city_id);

create table personal_profile
(
    id              int auto_increment
        primary key,
    birth_year      date         null,
    first_name      varchar(255) null,
    last_name       varchar(255) null,
    personal_review varchar(255) null,
    picture         varchar(255) null,
    location_id     int          null
)
    engine = MyISAM;

create index FK7fbfvwlfl9l3r660y7nr66lbw
    on personal_profile (location_id);

create table posts_table
(
    post_id   int auto_increment
        primary key,
    content   varchar(255) null,
    is_public bit          null,
    date_time varchar(255) null,
    formatter tinyblob     null,
    likes     int          null,
    picture   varchar(255) null,
    user_id   int          null
)
    engine = MyISAM;

create index FKbsjv9jobx8p2xsrr3ath7s1h2
    on posts_table (user_id);

create table roles
(
    role_id   int auto_increment
        primary key,
    authority varchar(255) null
)
    engine = MyISAM;

create table skills_table
(
    skill_id int          not null
        primary key,
    skill    varchar(255) null
)
    engine = MyISAM;

create table users
(
    user_id    int auto_increment
        primary key,
    password   varchar(255)         null,
    username   varchar(255)         null,
    email      varchar(255)         null,
    profile_id int                  null,
    enabled    tinyint(1) default 1 null,
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username)
)
    engine = MyISAM;

create index FK7idauxvm481tmluwq7yw5uydo
    on users (profile_id);

create table users_authorities
(
    user_id      int not null,
    authority_id int not null,
    primary key (user_id, authority_id)
)
    engine = MyISAM;

create index FKdsfxx5g8x8mnxne1fe0yxhjhq
    on users_authorities (authority_id);

create table users_roles
(
    user_id int not null,
    role_id int not null
)
    engine = MyISAM;

create index FK2o0jvgh89lemvvo17cbqvdxaa
    on users_roles (user_id);

create index FKj6m8fwv7oqv74fcehir1a9ffy
    on users_roles (role_id);


