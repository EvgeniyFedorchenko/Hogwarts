-- liquibase formatted sql

-- changeset evgeniy-fedorchenko:1
CREATE INDEX student_name_idx ON students (name);

-- changeset evgeniy-fedorchenko:2
CREATE INDEX faculty_name_color_idx ON faculties (name, color);