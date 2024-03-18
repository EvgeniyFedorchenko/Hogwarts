package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.dto.FacultyInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.mappers.FacultyMapper;
import com.evgeniyfedorchenko.hogwarts.mappers.StudentMapper;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final FacultyMapper facultyMapper;
    private final StudentMapper studentMapper;

    public FacultyServiceImpl(FacultyRepository facultyRepository,
                              StudentRepository studentRepository,
                              FacultyMapper facultyMapper,
                              StudentMapper studentMapper) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.facultyMapper = facultyMapper;
        this.studentMapper = studentMapper;
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
    public Optional<FacultyOutputDto> findFaculty(Long id) {
        return facultyRepository.findById(id).map(facultyMapper::toDto);

    }

    @Override
    public Optional<FacultyOutputDto> updateFaculty(Long id, FacultyInputDto facultyInputDto) {

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

    private Faculty fillFaculty(FacultyInputDto src, Faculty dest) {
        dest.setName(src.getName());
        dest.setColor(src.getColor());
        /* Если мы пришли сюда из метода update(), то List<Student> останется со старым наполнением,
           если из метода create(), то список будет null до первого вызова getStudents(), далее просто пустой список */
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
    public List<FacultyOutputDto> findFacultyByColorOrPartName(Color color, String namePart) {
        List<Faculty> faculties = color == null
                ? facultyRepository.findByNameContainsIgnoreCase(namePart)
                : facultyRepository.findFacultyByColorAndNameContainsIgnoreCase(color, namePart);

        return faculties.stream()
                .map(facultyMapper::toDto)
                .toList();
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
