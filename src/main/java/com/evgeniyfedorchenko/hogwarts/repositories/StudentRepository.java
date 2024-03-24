package com.evgeniyfedorchenko.hogwarts.repositories;

import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int min, int max);

    List<Student> findByFaculty_Id(Long id);

    @Query(value = "SELECT AVG(age) FROM students", nativeQuery = true)
    Double getAverageAge();

    @Query(value = "SELECT * FROM students ORDER BY :sortColumn ASC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Student> findLastStudentsAscSort(@Param("sortColumn") String sortParam,
                                          @Param("limit") int limit,
                                          @Param("offset") int offset);

    @Query(value = "SELECT * FROM students ORDER BY :sortColumn DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Student> findLastStudentsDescSort(@Param("sortColumn") String sortParam,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);
}
