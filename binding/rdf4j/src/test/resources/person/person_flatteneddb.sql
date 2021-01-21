create table "person" (
	"id" int primary key,
	"fullName" varchar(100) NOT NULL,
	"status" int NOT NULL,
	"country" varchar(100) NOT NULL,
	"locality" varchar(100) NOT NULL,
	"ssn" varchar(100) NOT NULL,
	"pos" int NOT NULL,
	"tags" int NOT NULL
);

insert into "person" ("id", "fullName", "status", "country", "locality", "ssn", "pos", "tags") values (1, 'Roger Smith', 1, 'it', 'Botzen', "IT1234567", 1, 34);