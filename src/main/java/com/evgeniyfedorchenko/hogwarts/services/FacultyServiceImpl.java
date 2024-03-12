package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyAlreadyExistsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.IllegalFacultyFieldsException;
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

        if (facultyRepository.existsByName(faculty.getName())) {
            throw new FacultyAlreadyExistsException("Such a faculty already exists");
        }
        return facultyRepository.save(fillFaculty(faculty, new Faculty()));
    }

    @Override
    public Optional<Faculty> findFaculty(Long id) {
        return facultyRepository.findById(id);

    }

    @Override
    public Optional<Faculty> updateFaculty(Long id, Faculty faculty) {

        validateFaculty(faculty);

        Optional<Faculty> byId = facultyRepository.findById(id);
        if (id <= 0L || byId.isEmpty()) {
            return Optional.empty();
        }

        Optional<Faculty> firstByName = facultyRepository.findFirstByName(faculty.getName());
        if (firstByName.isPresent() && !id.equals(firstByName.get().getId())) {
            throw new FacultyAlreadyExistsException("This name already exists");

        } else {
            Faculty oldFaculty = fillFaculty(faculty, byId.get());
            return Optional.of(facultyRepository.save(oldFaculty));
        }
    }

    private Faculty fillFaculty(Faculty src, Faculty dest) {
        dest.setName(src.getName());
        dest.setColor(src.getColor());
        dest.setStudents(src.getStudents());
        return dest;
    }

    @Override
    public Optional<Faculty> deleteFaculty(Long id) {

        Optional<Faculty> facultyOpt = facultyRepository.findById(id);
        if (facultyOpt.isPresent()) {
            List<Student> students = facultyOpt.get().getStudents();
            studentRepository.deleteAll(students);
            facultyRepository.delete(facultyOpt.get());
            return facultyOpt;
        } else {
            return Optional.empty();
        }
    }

    // Для того чтобы можно было искать по совпадению хотя бы одного параметра
    @Override
    public List<Faculty> findFacultyByColorOrPartName(Color color, String namePart) {
        return color == null
                ? facultyRepository.findByNameContainsIgnoreCase(namePart)
                : facultyRepository.findFacultyByColorAndNameContainsIgnoreCase(color, namePart);
    }

    @Override
    public List<Student> findStudents(Long id) {
        return studentRepository.findByFaculty_Id(id);
    }

    private void validateFaculty(Faculty faculty) {
        if (faculty.getName() == null) {
            throw new IllegalFacultyFieldsException(
                    "Faculty name cannot be null", "name", faculty.getName());
        } else if (faculty.getColor() == null) {
            throw new IllegalFacultyFieldsException(
                    "Faculty color cannot be null", "color", String.valueOf(faculty.getColor()));
        }
    }
}
