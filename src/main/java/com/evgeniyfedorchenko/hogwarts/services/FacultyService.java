package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;

import java.util.List;
import java.util.Optional;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Optional<Faculty> findFaculty(Long id);

    Optional<Faculty> updateFaculty(Faculty faculty);

    Optional<Faculty> deleteFaculty(Long id);

    List<Faculty> findFacultyByColorOrPartName(Color color, String name);

    List<Student> findStudents(Long id);
}
