package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Student;

import java.util.Optional;

public interface StudentService {

    Student createStudent(Student student);

    Optional<Student> getStudent(Long id);

    Optional<Student> updateStudent(Long id, Student student);

    Optional<Student> deleteStudent(Long id);
}
