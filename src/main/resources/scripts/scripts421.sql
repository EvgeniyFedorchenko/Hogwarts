ALTER TABLE students
    ADD CONSTRAINT age_greater_than_zero CHECK (age > 16),  -- Возраст студента не может быть меньше 16 лет
    ADD CONSTRAINT nickname_unique UNIQUE (name),           -- Имена студентов должны быть уникальными
    ALTER COLUMN name SET NOT NULL,                         -- ...и не равны нулю
    ALTER COLUMN age SET DEFAULT 20;                        -- При создании студента без возраста ему должно присваиваться 20 лет.
UPDATE students SET age = 20 WHERE age IS NULL;             -- Заменяем возраст уже созданных студентов, если он попадает под предыдущее ограничение


-- Пара “значение названия” - “цвет факультета” должна быть уникальной.
ALTER TABLE faculties
    ADD CONSTRAINT name_color_unique UNIQUE (name, color);