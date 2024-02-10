package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Faculty;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Faculty getFaculty(Long id);

    Faculty updateFaculty(Long id, Faculty faculty);

    Faculty deleteFaculty(Long id);
}
