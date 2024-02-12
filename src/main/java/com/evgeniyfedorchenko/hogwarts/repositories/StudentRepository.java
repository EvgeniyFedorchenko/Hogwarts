package com.evgeniyfedorchenko.hogwarts.repositories;

import com.evgeniyfedorchenko.hogwarts.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
