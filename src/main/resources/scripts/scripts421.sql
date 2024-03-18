ALTER TABLE students
    ADD CONSTRAINT age_greater_than_zero CHECK (age >= 16),
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE faculties
    ADD CONSTRAINT name_color_unique UNIQUE (name, color);