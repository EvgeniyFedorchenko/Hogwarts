package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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
            facultyRepository.delete(facultyOpt.get());
            return facultyOpt;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Faculty> getFacultyWithColor(Color color) {
        return facultyRepository.findByColor(color);
    }

    private void validateFaculty(Faculty faculty) {
        if (faculty.getName() == null || faculty.getColor() == null) {
            throw new IllegalFacultyFieldsException("Any faculty's field cannot be null");
        }
    }

    private void findAlreadyBeingFacultiesWithThisName(String name) {
        if (facultyRepository.existsByName(name)) {
            throw new IllegalFacultyFieldsException("This faculty already exist");
        }
    }
}
