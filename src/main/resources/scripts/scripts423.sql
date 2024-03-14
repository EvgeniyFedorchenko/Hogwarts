SELECT
    students.name AS students_name,
	students.age AS students_age,
	faculties.name AS faculties_name
FROM
    students
INNER JOIN
    faculties ON students.faculty_id = faculties.id;

SELECT
    students.name AS students_name,
	students.age AS students_age,
	avatars
FROM
    students
INNER JOIN
    avatars ON students.avatar_id = avatars.id;