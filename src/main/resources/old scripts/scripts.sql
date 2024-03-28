-- Получить всех студентов, возраст которых находится между 10 и 20 (можно подставить любые числа, главное, чтобы нижняя граница была меньше верхней).
-- Получить всех студентов, но отобразить только список их имен.
-- Получить всех студентов, у которых в имени присутствует буква «О» (или любая другая).
-- Получить всех студентов, у которых возраст меньше идентификатора.
-- Получить всех студентов упорядоченных по возрасту.

SELECT * FROM students BETWEEN 10 and 20;

SELECT name FROM students;

SELECT * FROM students WHERE LOWER(name) LIKE '%o%';

SELECT * FROM students WHERE age > students.id;

SELECT * FROM students ORDER BY age;
select * from databasechangelog;
drop table databasechangelog
