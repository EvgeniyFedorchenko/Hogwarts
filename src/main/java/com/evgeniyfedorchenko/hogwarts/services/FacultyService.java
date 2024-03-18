package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.dto.FacultyInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;

import java.util.List;
import java.util.Optional;

public interface FacultyService {

    FacultyOutputDto createFaculty(FacultyInputDto facultyInputDto);

    Optional<FacultyOutputDto> findFaculty(Long id);

    Optional<FacultyOutputDto> updateFaculty(Long id, FacultyInputDto facultyInputDto);

    Optional<Faculty> deleteFaculty(Long id);

    List<FacultyOutputDto> findFacultyByColorOrPartName(Color color, String namePart);

    List<StudentOutputDto> findStudents(Long id);
}
