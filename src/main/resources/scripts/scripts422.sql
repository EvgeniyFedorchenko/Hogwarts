-- У каждого человека есть машина. Причем несколько человек могут пользоваться одной машиной.
-- У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет).
-- У каждой машины есть марка, модель и стоимость.

CREATE TABLE cars (
    id INT PRIMARY KEY,
	brand VARCHAR NOT NULL,
	model VARCHAR NOT NULL,
	cost NUMERIC CHECK (cost > 0) NOT NULL
);

CREATE TABLE people (
	id INT PRIMARY KEY,
    name VARCHAR NOT NULL,
	age SMALLINT CHECK (age > 18),
	has_license BOOLEAN DEFAULT FALSE,
);

-- Связываем таблицы (Одному человеку нельзя иметь несколько машин)
CREATE TABLE car_ownership (
    ownership_id INT PRIMARY KEY,
    person_id INT REFERENCES people (id) UNIQUE,
    car_id INT REFERENCES cars (id)
);

-- Предположим, что у вас есть две машины с id = 1 и 2 и три человека с id = 101 и 102, 103
INSERT INTO car_ownership (car_id, person_id)
VALUES
    (101, 1), -- Первый человек
    (102, 2), -- Второй человек
    (103, 2), -- Третий человек
    (101, 2); -- Ошибка