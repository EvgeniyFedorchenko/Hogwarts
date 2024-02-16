package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student createStudent(Student student);

    Optional<Student> getStudent(Long id);

    Optional<Student> updateStudent(Student student);

    Optional<Student> deleteStudent(Long id);

    List<Student> getStudentWithAge(int age);
}
