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
INSERT INTO sektion VALUES(2);
INSERT INTO sektion VALUES(3);
INSERT INTO sektion VALUES(4);
INSERT INTO sektion VALUES(5);

create table voting_option
(
    type text not null
        constraint voting_option_pk
            primary key
);
INSERT INTO voting_option VALUES('INSTANT_RUNOFF_VOTE');
INSERT INTO voting_option VALUES('SINGLE_TRANSFERABLE_VOTE');

create table role
(
    name text not null
        constraint role_pk
            primary key,
    voting_option_type text not null
        constraint role_voting_option_type_fk
            references voting_option
            on update restrict on delete restrict
);
INSERT INTO role VALUES('Vorsitz', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Finanzreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Schriftführung', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Katasterführung', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Frauenreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Bildungsreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Umweltreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Jugendreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Migrationsreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Medienreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Gewerkschaftsreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Wirtschaftsreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Pensionistenreferat', 'INSTANT_RUNOFF_VOTE');
INSERT INTO role VALUES('Bezirkskonferenz', 'SINGLE_TRANSFERABLE_VOTE');
INSERT INTO role VALUES('Bezirksrat', 'SINGLE_TRANSFERABLE_VOTE');

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

create table role_gender_blacklist
(
    role text not null
        constraint role_gender_blacklist_role_name_fk
            references role
            on update restrict on delete restrict,
    gender text not null
        constraint role_gender_blacklist_pk
            primary key
        constraint role_gender_blacklist_gender_name_fk
            references gender
            on update restrict on delete restrict
);
INSERT INTO role_gender_blacklist VALUES('Frauenreferat', 'Männlich');

create unique index voting_option_type_uindex
    on voting_option (type);
CREATE UNIQUE INDEX user_username_uindex
	on user (username);
CREATE UNIQUE INDEX role_name_uindex
	on role (name);
CREATE UNIQUE INDEX gender_name_uindex
	on gender (name);