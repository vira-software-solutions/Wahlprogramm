CREATE TABLE user
(
	username text not null
		constraint user_pk
			primary key,
	password text not null
);
CREATE TABLE IF NOT EXISTS "sektion"
(
	num int not null
		constraint sektion_pk
			primary key
);
INSERT INTO sektion VALUES(1);
INSERT INTO sektion VALUES(2);
INSERT INTO sektion VALUES(3);
INSERT INTO sektion VALUES(4);
INSERT INTO sektion VALUES(5);
INSERT INTO sektion VALUES(6);
INSERT INTO sektion VALUES(7);
INSERT INTO sektion VALUES(8);
INSERT INTO sektion VALUES(9);
INSERT INTO sektion VALUES(10);
INSERT INTO sektion VALUES(11);
INSERT INTO sektion VALUES(12);
INSERT INTO sektion VALUES(13);
INSERT INTO sektion VALUES(14);
INSERT INTO sektion VALUES(15);
INSERT INTO sektion VALUES(16);
INSERT INTO sektion VALUES(18);
INSERT INTO sektion VALUES(20);
INSERT INTO sektion VALUES(22);
INSERT INTO sektion VALUES(26);
INSERT INTO sektion VALUES(29);
INSERT INTO sektion VALUES(31);
INSERT INTO sektion VALUES(32);
INSERT INTO sektion VALUES(33);
INSERT INTO sektion VALUES(34);
CREATE TABLE IF NOT EXISTS "role"
(
	name text not null
		constraint role_pk
			primary key
);
INSERT INTO role VALUES('Vorsitz');
INSERT INTO role VALUES('Finanzreferat');
INSERT INTO role VALUES('Schriftführung');
INSERT INTO role VALUES('Katasterführung');
INSERT INTO role VALUES('Fraunreferat');
INSERT INTO role VALUES('Bildungsreferat');
INSERT INTO role VALUES('Umweltreferat');
INSERT INTO role VALUES('Jugendreferat');
INSERT INTO role VALUES('Migrationsreferat');
INSERT INTO role VALUES('Medienreferat');
INSERT INTO role VALUES('Gewerkschaftsreferat');
INSERT INTO role VALUES('Wirtschaftsreferat');
CREATE TABLE IF NOT EXISTS "role_sektion_candidate"
(
	sektion_num int not null
		references sektion
			on update restrict on delete restrict,
	role_name text not null
		references role
			on update restrict on delete restrict,
	candidate_name text
		references candidate
			on update restrict on delete restrict,
	constraint role_sektion_pk
		primary key (role_name, sektion_num, candidate_name)
);
CREATE TABLE IF NOT EXISTS "candidate"
(
	name text not null
		constraint candidates_pk
			primary key,
	gender text
		references gender
			on update restrict on delete restrict
);
CREATE TABLE gender
(
	name text not null
		constraint gender_pk
			primary key
);
INSERT INTO gender VALUES('Männlich');
INSERT INTO gender VALUES('Weiblich');
INSERT INTO gender VALUES('Sonstige');

CREATE UNIQUE INDEX user_username_uindex
	on user (username);
CREATE UNIQUE INDEX role_name_uindex
	on role (name);
CREATE UNIQUE INDEX gender_name_uindex
	on gender (name);

COMMIT;