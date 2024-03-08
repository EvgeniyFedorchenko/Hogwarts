package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student createStudent(Student student);

    Optional<Student> findStudent(Long id);

    Optional<Student> updateStudent(Long id, Student student);

    Optional<Student> deleteStudent(Long id);

    List<Student> findStudentsByExactAge(int age);

    List<Student> findStudentsByAgeBetween(int min, int max);

    Optional<Faculty> findFaculty(Long id);

    boolean setAvatar(Long id, MultipartFile avatarFile);

    Avatar getAvatar(Long id, boolean large);
}
