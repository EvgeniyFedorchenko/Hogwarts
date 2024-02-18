package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyAlreadyExistsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository,
                              StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        validateFaculty(faculty);
        findAlreadyBeingFacultiesWithThisName(faculty.getName());

        Faculty newFaculty = new Faculty();
        newFaculty.setName(faculty.getName());
        newFaculty.setColor(faculty.getColor());
        newFaculty.setStudents(faculty.getStudents());

        return facultyRepository.save(newFaculty);
    }

    @Override
    public Optional<Faculty> findFaculty(Long id) {
        return facultyRepository.findById(id);

    }

    @Override
    public Optional<Faculty> updateFaculty(Faculty faculty) {
        validateFaculty(faculty);
        if (faculty.getId() == null || faculty.getId() <= 0L) {
            return Optional.empty();
        }
        Faculty alreadyBeingFaculty = facultyRepository.findFirstByName(faculty.getName());
        if (alreadyBeingFaculty != null && alreadyBeingFaculty.getId().equals(faculty.getId())) {

            if (facultyRepository.findById(faculty.getId()).isPresent()) {
                return Optional.of(facultyRepository.save(faculty));
            }
        }
        return Optional.empty();
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
    public List<Faculty> findFacultyByColorOrPartName(String colorOrName) {
        return facultyRepository.findFacultyByColorOrNameContainsIgnoreCase(Color.valueOf(colorOrName), colorOrName);
    }

    @Override
    public List<Student> findStudents(Long id) {
        return studentRepository.findByFaculty_Id(id);
    }

    private void validateFaculty(Faculty faculty) {
        if (faculty.getName() == null) {
            throw new IllegalFacultyFieldsException("Faculty name cannot be null", "name", faculty.getName());
        } else if (faculty.getColor() == null) {
            throw new IllegalFacultyFieldsException(
                    "Faculty color cannot be null", "color", String.valueOf(faculty.getColor()));
        }
    }

    private void findAlreadyBeingFacultiesWithThisName(String name) {
        if (facultyRepository.existsByName(name)) {
            throw new FacultyAlreadyExistsException("Such a faculty already exists");
        }
    }
}
