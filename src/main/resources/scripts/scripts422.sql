-- У каждого человека есть машина. Причем несколько человек могут пользоваться одной машиной.
-- У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет).
-- У каждой машины есть марка, модель и стоимость.

CREATE TABLE cars (
    id BIGSERIAL PRIMARY KEY,
	brand VARCHAR NOT NULL,
	model VARCHAR NOT NULL,
	cost NUMERIC NOT NULL CHECK (cost > 0)
);

CREATE TABLE people (
	id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
	age SMALLINT CHECK (age >= 18),
	has_license BOOLEAN DEFAULT FALSE,
	car_id BIGINT REFERENCES cars (id);
);