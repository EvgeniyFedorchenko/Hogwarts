package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Faculty;

import java.util.Optional;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Optional<Faculty> getFaculty(Long id);

    Optional<Faculty> updateFaculty(Long id, Faculty faculty);

    Optional<Faculty> deleteFaculty(Long id);
}
