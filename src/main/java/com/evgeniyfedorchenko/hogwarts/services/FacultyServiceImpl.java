package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        validateFaculty(faculty);
        faculty.setId(0L);
        return facultyRepository.save(faculty);
    }

    @Override
    public Optional<Faculty> getFaculty(Long id) {
        return facultyRepository.findById(id);

    }

    @Override
    public Optional<Faculty> updateFaculty(Faculty faculty) {
        validateFaculty(faculty);
        findAlreadyBeingFacultiesWithThisName(faculty.getName());

        return facultyRepository.findById(faculty.getId()).isPresent()
                ? Optional.of(facultyRepository.save(faculty))
                : Optional.empty();
    }

    @Override
    public Optional<Faculty> deleteFaculty(Long id) {

        Optional<Faculty> facultyOpt = facultyRepository.findById(id);
        if (facultyOpt.isPresent()) {
            facultyRepository.deleteById(id);
            return facultyOpt;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Faculty> getFacultyWithColor(Color color) {
        return facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getColor() == color)
                .collect(Collectors.toList());
    }

    private void validateFaculty(Faculty faculty) {
        if (faculty.getName() == null || faculty.getColor() == null) {
            throw new IllegalFacultyFieldsException("Any faculty's field cannot be null");
        }
    }

    private void findAlreadyBeingFacultiesWithThisName(String name) {
        List<Faculty> byNameLike = facultyRepository.findByNameLike(name);
        if (!byNameLike.isEmpty()) {
            throw new IllegalFacultyFieldsException("This faculty already exist");
        }
    }
}
