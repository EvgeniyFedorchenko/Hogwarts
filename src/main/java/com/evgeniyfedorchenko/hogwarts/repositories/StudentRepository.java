package com.evgeniyfedorchenko.hogwarts.repositories;

import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int min, int max);

    List<Student> findByFaculty_Id(Long id);

    @Query(value = "SELECT AVG(age) FROM students", nativeQuery = true)
    int getAverageAge();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT :q", nativeQuery = true)
    List<Student> findLastStudents(@Param("q") int quantity);
}
