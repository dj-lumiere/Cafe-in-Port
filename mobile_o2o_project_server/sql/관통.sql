drop database if exists ssafy_mobile_cafe;
select @@global.transaction_isolation, @@transaction_isolation;
set @@transaction_isolation = "read-committed";

create database ssafy_mobile_cafe;
use ssafy_mobile_cafe;

create table t_user
(
    user_no       integer auto_increment primary key not null,
    id            varchar(100)                       not null unique,
    username      varchar(100)                       not null,
    pass          varchar(256)                       not null default '',
    email         varchar(100)                       not null unique,
    register_time timestamp                                   default CURRENT_TIMESTAMP,
    stamps        integer                                     default 0,
    fcm_token     varchar(512)                       not null default ''
);

create table t_product
(
    id   integer auto_increment primary key         not null,
    name varchar(100)                               not null,
    type ENUM ('NO','COFFEE', 'NON_COFFEE', 'FOOD') not null default 'NO',
    img  varchar(255)                               not null
);

create table t_product_detail
(
    id          integer auto_increment primary key         not null,
    p_id        integer                                    not null,
    size        ENUM ('NO', 'PETITE', 'REGULAR', 'GRANDE') not null default 'NO',
    temperature ENUM ('NO', 'HOT', 'COLD')                 not null default 'NO',
    price       integer                                    not null,
    constraint fk_p_id foreign key (p_id) references t_product (id) on delete cascade
);

create table t_order
(
    o_id        integer auto_increment primary key,
    user_no     integer not null,
    order_table varchar(20),
    create_time timestamp                                                                        default CURRENT_TIMESTAMP,
    update_time timestamp                                                                        default CURRENT_TIMESTAMP,
    status      ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED', 'RETURNED', 'REFUNDED') DEFAULT 'PENDING',
    constraint fk_order_user foreign key (user_no) references t_user (user_no) on delete cascade
);

create table t_order_detail
(
    d_id              integer auto_increment primary key,
    order_id          integer not null,
    product_detail_id integer not null,
    quantity          integer not null default 1,
    constraint fk_order_detail_product foreign key (product_detail_id) references t_product_detail (id) on delete cascade,
    constraint fk_order_detail_order foreign key (order_id) references t_order (o_id) on delete cascade
);

create table t_stamp
(
    id       integer auto_increment primary key,
    user_no  integer not null,
    order_id integer not null,
    quantity integer not null default 1,
    constraint fk_stamp_user foreign key (user_no) references t_user (user_no) on delete cascade,
    constraint fk_stamp_order foreign key (order_id) references t_order (o_id) on delete cascade
);

create table t_comment
(
    id         integer auto_increment primary key,
    user_no    integer not null,
    product_id integer not null,
    rating     float   not null default 1,
    comment    varchar(200),
    constraint fk_comment_user foreign key (user_no) references t_user (user_no) on delete cascade,
    constraint fk_comment_product foreign key (product_id) references t_product (id) on delete cascade
);

INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 01', 'name 01', 'pass 01', 4, 'asdf@gmail.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 02', 'name 02', 'pass 02', 1, 'qweq@gmail.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 03', 'name 03', 'pass 03', 3, 'asdfasdf@gmail.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 04', 'name 04', 'pass 04', 4, 'asdf@naver.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 05', 'name 05', 'pass 05', 5, 'qwer@naver.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 06', 'name 06', 'pass 06', 6, 'zxcv@naver.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 07', 'name 07', 'pass 07', 7, '1111@gmail.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 08', 'name 08', 'pass 08', 8, '2222@gmail.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 09', 'name 09', 'pass 09', 9, '3333@gmail.com');
INSERT INTO t_user (id, username, pass, stamps, email)
VALUES ('id 10', 'name 10', 'pass 10', 10, '4444@gmail.com');

insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (1, '아메리카노', 'COFFEE', 'menu_americano.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (2, '카페라떼', 'COFFEE', 'menu_caffelatte.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (3, '바닐라라떼', 'COFFEE', 'menu_vanillalatte.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (4, '빙수', 'FOOD', 'menu_bingsu.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (5, '카야토스트', 'FOOD', 'menu_kayatoast.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (6, '앙버터크로아상', 'FOOD', 'menu_redbeanbuttercroissant.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (7, '샌드위치', 'FOOD', 'menu_sandwich.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (8, '티라미수', 'FOOD', 'menu_tiramisu.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (9, '초코칩쿠키', 'FOOD', 'menu_chocolatechipcookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (10, '초콜릿쿠키', 'FOOD', 'menu_chocolatecookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (11, '초코크럼블쿠키', 'FOOD', 'menu_chocolatecrumblecookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (12, '시나몬드리즐쿠키', 'FOOD', 'menu_cinnamondrizzlecookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (13, '듀얼쿠키', 'FOOD', 'menu_dualcookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (14, '로터스쿠키', 'FOOD', 'menu_lotuscookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (15, '마시멜로쿠키', 'FOOD', 'menu_marshmallowcookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (16, '오레오쿠키', 'FOOD', 'menu_oreocookie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (17, '블루베리스무디', 'NON_COFFEE', 'menu_blueberrysmoothie.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (18, '초콜릿라떼', 'NON_COFFEE', 'menu_chocolatelatte.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (19, '자몽에이드', 'NON_COFFEE', 'menu_grapefruitade.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (20, '망고사고', 'NON_COFFEE', 'menu_mangosago.jpg');
insert into ssafy_mobile_cafe.t_product (id, name, type, img)
values (21, '오미자티', 'NON_COFFEE', 'menu_omijatea.jpg');

insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (1, 1, 'PETITE', 'HOT', 2200);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (2, 1, 'REGULAR', 'HOT', 2700);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (3, 1, 'GRANDE', 'HOT', 3400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (4, 1, 'PETITE', 'COLD', 2200);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (5, 1, 'REGULAR', 'COLD', 2700);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (6, 1, 'GRANDE', 'COLD', 3400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (7, 2, 'PETITE', 'HOT', 2500);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (8, 2, 'REGULAR', 'HOT', 3000);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (9, 2, 'GRANDE', 'HOT', 3700);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (10, 2, 'PETITE', 'COLD', 2500);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (11, 2, 'REGULAR', 'COLD', 3000);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (12, 2, 'GRANDE', 'COLD', 3700);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (13, 3, 'PETITE', 'HOT', 2600);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (14, 3, 'REGULAR', 'HOT', 3100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (15, 3, 'GRANDE', 'HOT', 3800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (16, 3, 'PETITE', 'COLD', 2600);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (17, 3, 'REGULAR', 'COLD', 3100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (18, 3, 'GRANDE', 'COLD', 3800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (19, 4, 'NO', 'NO', 5900);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (20, 5, 'NO', 'NO', 6900);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (21, 6, 'NO', 'NO', 6600);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (22, 7, 'NO', 'NO', 6800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (23, 8, 'NO', 'NO', 6400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (24, 9, 'NO', 'NO', 1500);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (25, 10, 'NO', 'NO', 1200);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (26, 11, 'NO', 'NO', 1400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (27, 12, 'NO', 'NO', 1100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (28, 13, 'NO', 'NO', 1300);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (29, 14, 'NO', 'NO', 1400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (30, 15, 'NO', 'NO', 1000);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (31, 16, 'NO', 'NO', 1400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (32, 17, 'PETITE', 'COLD', 2600);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (33, 17, 'REGULAR', 'COLD', 3100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (34, 17, 'GRANDE', 'COLD', 3800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (35, 18, 'PETITE', 'HOT', 2800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (36, 18, 'REGULAR', 'HOT', 3300);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (37, 18, 'GRANDE', 'HOT', 4000);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (38, 18, 'PETITE', 'COLD', 2800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (39, 18, 'REGULAR', 'COLD', 3300);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (40, 18, 'GRANDE', 'COLD', 4000);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (41, 19, 'PETITE', 'COLD', 2900);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (42, 19, 'REGULAR', 'COLD', 3400);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (43, 19, 'GRANDE', 'COLD', 4100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (44, 20, 'PETITE', 'COLD', 3500);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (45, 20, 'REGULAR', 'COLD', 4000);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (46, 20, 'GRANDE', 'COLD', 4700);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (47, 21, 'PETITE', 'HOT', 3600);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (48, 21, 'REGULAR', 'HOT', 4100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (49, 21, 'GRANDE', 'HOT', 4800);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (50, 21, 'PETITE', 'COLD', 3600);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (51, 21, 'REGULAR', 'COLD', 4100);
insert into ssafy_mobile_cafe.t_product_detail (id, p_id, size, temperature, price)
values (52, 21, 'GRANDE', 'COLD', 4800);

commit;
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (1, 1, 1, 'comment 01');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (2, 1, 2, 'comment 02');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (3, 1, 3, 'comment 03');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (4, 4, 4, 'comment 04');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (5, 5, 5, 'comment 05');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (6, 6, 6, 'comment 06');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (7, 7, 7, 'comment 07');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (8, 8, 8, 'comment 08');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (9, 9, 9, 'comment 09');
INSERT INTO t_comment (user_no, product_id, rating, comment)
VALUES (10, 10, 10, 'comment 10');

INSERT INTO t_order (user_no, order_table)
VALUES (1, 'order_table 01');
INSERT INTO t_order (user_no, order_table)
VALUES (2, 'order_table 02');
INSERT INTO t_order (user_no, order_table)
VALUES (3, 'order_table 03');
INSERT INTO t_order (user_no, order_table)
VALUES (4, 'order_table 04');
INSERT INTO t_order (user_no, order_table)
VALUES (5, 'order_table 05');
INSERT INTO t_order (user_no, order_table)
VALUES (6, 'order_table 06');
INSERT INTO t_order (user_no, order_table)
VALUES (7, 'order_table 07');
INSERT INTO t_order (user_no, order_table)
VALUES (8, 'order_table 08');
INSERT INTO t_order (user_no, order_table)
VALUES (9, 'order_table 09');
INSERT INTO t_order (user_no, order_table)
VALUES (10, 'order_table 10');

INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (1, 1, 1);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (1, 2, 3);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (2, 1, 1);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (3, 3, 3);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (4, 4, 4);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (5, 5, 5);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (6, 6, 6);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (7, 7, 7);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (8, 8, 8);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (9, 9, 9);
INSERT INTO t_order_detail (order_id, product_detail_id, quantity)
VALUES (10, 10, 10);

INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (1, 1, 4);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (2, 2, 1);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (3, 3, 3);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (4, 4, 4);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (5, 5, 5);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (6, 6, 6);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (7, 7, 7);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (8, 8, 8);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (9, 9, 9);
INSERT INTO t_stamp (user_no, order_id, quantity)
VALUES (10, 10, 10);

commit;
