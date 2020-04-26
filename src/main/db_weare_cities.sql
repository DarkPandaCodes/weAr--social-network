create table cities
(
    city_id    int auto_increment
        primary key,
    city       varchar(255) null,
    country_id int          null,
    constraint FK6gatmv9dwedve82icy8wrkdmk
        foreign key (country_id) references countries (id)
);

INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (1, 'Sofia', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (2, 'Plovdiv', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (3, 'Varna', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (4, 'Burgas', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (5, 'Ruse', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (6, 'Stara Zagora', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (7, 'Pleven', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (8, 'Sliven', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (9, 'Dobrich', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (10, 'Shumen', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (11, 'Pernik', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (12, 'Haskovo', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (13, 'Vratsa', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (14, 'Kyustendil', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (15, 'Montana', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (16, 'Lovech', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (17, 'Razgrad', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (18, 'Borino', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (19, 'Madan', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (20, 'Zlatograd', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (21, 'Pazardzhik', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (22, 'Smolyan', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (23, 'Blagoevgrad', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (24, 'Nedelino', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (25, 'Rudozem', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (26, 'Devin', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (27, 'Veliko Tarnovo', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (28, 'Vidin', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (29, 'Kirkovo', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (30, 'Krumovgrad', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (31, 'Dzhebel', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (32, 'Silistra', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (33, 'Sarnitsa', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (34, 'Shiroka Laka', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (35, 'Yambol', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (36, 'Dospat', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (37, 'Kardzhali', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (38, 'Gabrovo', 1);
INSERT INTO db_weare.cities (city_id, city, country_id) VALUES (39, 'Targovishte', 1);