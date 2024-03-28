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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final FacultyMapper facultyMapper;
    private final StudentMapper studentMapper;
    private final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

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
    public FacultyOutputDto createFaculty(FacultyInputDto inputDto) {

        Faculty faculty = fillFaculty(inputDto, new Faculty());
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("New {} successfully saved", faculty);
        return facultyMapper.toDto(savedFaculty);
    }

    @Override
    public Optional<FacultyOutputDto> findFaculty(Long id) {
        Optional<FacultyOutputDto> outputDtoOpt = facultyRepository.findById(id).map(facultyMapper::toDto);
        logger.debug("FacultyID %s ".formatted(id) + (outputDtoOpt.isEmpty() ? "not found" : "was found") + "for find");
        return outputDtoOpt;

    }

    @Override
    public Optional<FacultyOutputDto> updateFaculty(Long id, FacultyInputDto facultyInputDto) {

        Optional<Faculty> byId = facultyRepository.findById(id);
        if (id <= 0L || byId.isEmpty()) {
            logger.debug("FacultyID {} not found for update", id);
            return Optional.empty();
        }
        Faculty oldFaculty = fillFaculty(facultyInputDto, byId.get());
        facultyRepository.save(oldFaculty);
        logger.info("{} successfully updated to {}", byId, oldFaculty);

        return Optional.of(facultyMapper.toDto(oldFaculty));
    }

    private Faculty fillFaculty(FacultyInputDto src, Faculty dest) {
        dest.setName(src.getName());
        dest.setColor(src.getColor());
        /* Если мы пришли сюда из метода update(), то List<Student> останется со старым наполнением,
           если из метода create(), то список будет null до первого вызова getStudents(), далее просто пустой список */
        return dest;
    }

    @Override
    @Transactional
    public Optional<Faculty> deleteFaculty(Long id) {

        Optional<Faculty> facultyOpt = facultyRepository.findById(id);
        if (facultyOpt.isPresent()) {
            List<Student> students = facultyOpt.get().getStudents();
            studentRepository.deleteAll(students);
            facultyRepository.delete(facultyOpt.get());
            logger.info("{} successfully deleted with its students", facultyOpt.get());
            return facultyOpt;
        } else {
            logger.debug("FacultyID {} not found for delete", id);
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
    public List<StudentOutputDto> findStudents(Long id) {
        return studentRepository.findByFaculty_Id(id).stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public Optional<String> findLongestName() {

        AtomicReference<String> longestName = new AtomicReference<>("");

        logger.debug("Invoke non-optimal method \"facultyRepository.findAll()\"");
        facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .forEach(name -> {
                    if (longestName.get().length() < name.length()) {
                        longestName.set(name);
                    }
                });
        return longestName.get().equals("") ? Optional.empty() : Optional.of(longestName.get());

        /* Да, можно отсортировать по длине строки и вернуть самую длинную,
            но мне кажется так быстрее - просто один раз пройтись по списку имен и каждое сравнивать
            P.S. Компилятор не дает сделать переменную обычной строкой и присваивать ей в стриме новое значение,
                 так что пришлось сделать вот так */
    }
}
