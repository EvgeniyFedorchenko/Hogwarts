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

        if (facultyRepository.existsByName(faculty.getName())) {
            throw new FacultyAlreadyExistsException("Such a faculty already exists");
        }
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
    public Optional<Faculty> updateFaculty(Long id, Faculty faculty) {
        validateFaculty(faculty);

        Optional<Faculty> byId = facultyRepository.findById(id);
        if (id <= 0L || byId.isEmpty()) {
            return Optional.empty();
        }

        // Если факультет с таким именем уже есть в БД и это другой факультет (у него другой id) - прерываем метод
        Faculty firstByName = facultyRepository.findFirstByName(faculty.getName());
        if (firstByName != null && !id.equals(firstByName.getId())) {
            return Optional.empty();

            // Иначе - берем факультет и меняем его
        } else {
            Faculty oldFaculty = byId.get();
            oldFaculty.setName(faculty.getName());
            oldFaculty.setColor(faculty.getColor());
            oldFaculty.setStudents(faculty.getStudents());
            return Optional.of(facultyRepository.save(oldFaculty));
        }
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
