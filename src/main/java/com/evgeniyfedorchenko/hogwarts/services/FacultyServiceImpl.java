package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) throws IllegalFacultyFieldsException {
        validateFaculty(faculty);
        findAlreadyBeingFacultiesWithThisName(faculty.getName());

        Faculty newFaculty = new Faculty();
        newFaculty.setName(faculty.getName());
        newFaculty.setColor(faculty.getColor());

        return facultyRepository.save(newFaculty);
    }

    @Override
    public Optional<Faculty> findFaculty(Long id) {
        return facultyRepository.findById(id);

    }

    @Override
    public Optional<Faculty> updateFaculty(Faculty faculty) {
        validateFaculty(faculty);

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
    public Optional<List<Faculty>> findFacultyByColorOrPartName(Color color, String name) {
        List<Faculty> faculties = facultyRepository.findFacultyByColorOrNameContainsIgnoreCase(color, name);
        return faculties.isEmpty() ? Optional.empty() : Optional.of(faculties);
    }

    // TODO: 17.02.2024 не забыть добавить новые поля
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
