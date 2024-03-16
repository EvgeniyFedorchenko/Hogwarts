-- Получаем ВСЕХ студентов (имя, возраст) с факультетами (название)
SELECT
    students.name AS student_name,
	students.age AS student_age,
	faculties.name AS faculty_name
FROM
    students
LEFT JOIN
    faculties ON students.faculty_id = faculties.id;


-- Получаем студентов (имя, возраст), у которых есть аватары и сами аватары
SELECT
    students.name AS student_name,
	students.age AS student_age,
	avatars
FROM
    students
INNER JOIN
    avatars ON students.avatar_id = avatars.id;