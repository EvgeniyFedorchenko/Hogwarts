package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.controllers.SortOrder;
import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.exceptions.EntityNotFoundException;
import com.evgeniyfedorchenko.hogwarts.mappers.FacultyMapper;
import com.evgeniyfedorchenko.hogwarts.mappers.StudentMapper;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarService avatarService;
    private final StudentMapper studentMapper;
    private final FacultyMapper facultyMapper;
    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);


    public StudentServiceImpl(StudentRepository studentRepository,
                              FacultyRepository facultyRepository,
                              AvatarService avatarService,
                              StudentMapper studentMapper,
                              FacultyMapper facultyMapper) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarService = avatarService;
        this.studentMapper = studentMapper;
        this.facultyMapper = facultyMapper;
    }

    @Override
    @Transactional
    public StudentOutputDto createStudent(StudentInputDto inputDto) {
        Student student = fillStudent(inputDto, new Student());
        Student savedStudent = studentRepository.save(student);

        Faculty findedFaculty = findFaculty(inputDto.getFacultyId());
        findedFaculty.addStudent(savedStudent);
        facultyRepository.save(findedFaculty);
        
        logger.info("New {} successfully saved", savedStudent);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    public Optional<StudentOutputDto> findStudent(Long id) {
        return studentRepository.findById(id)
                .map(studentMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<StudentOutputDto> updateStudent(Long id, StudentInputDto inputDto) {
        if (id <= 0L) {
            return Optional.empty();
        }

        Optional<Student> studentById = studentRepository.findById(id);
        if (studentById.isEmpty()) {
            logger.debug("StudentID {} not found for update", id);
            return Optional.empty();
        }
        Student student = fillStudent(inputDto, studentById.get());
        studentRepository.save(student);
        
        logger.info("{} successfully updated to {}", studentById.get(), student);
        return Optional.of(studentMapper.toDto(student));
    }

    private Student fillStudent(StudentInputDto src, Student dest) {

        Faculty findedFaculty = findFaculty(src.getFacultyId());

        dest.setName(src.getName());
        dest.setAge(src.getAge());
//        Аватар, если он есть, остается на месте

//          dest.getFaculty() может быть null, если мы пришли сюда из метода create()
        if (dest.getFaculty() != null && !dest.getFaculty().equals(findedFaculty)) {
            facultyRepository.save(dest.getFaculty().removeStudent(dest));
            facultyRepository.save(findedFaculty.addStudent(dest));
            logger.debug("At {} changed faculty from {} to {}", dest, dest.getFaculty(), findedFaculty);
        } else {
            dest.setFaculty(findedFaculty);
        }
        return dest;
    }

    @Override
    @Transactional
    public List<StudentOutputDto> searchStudents(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        List<Student> students = sortOrder == SortOrder.ASC
                ? studentRepository.findLastStudentsAscSort(sortParam, pageSize, offset)
                : studentRepository.findLastStudentsDescSort(sortParam, pageSize, offset);
        logger.debug("Calling searchStudents with params: sortParam={}, sortOrder={}, pageNumber={}, pageSize={} returned student's ids: {}",
                sortParam, sortOrder, pageNumber, pageSize, students.stream().map(Student::getId).toList());
        return students.stream()
                .map(studentMapper::toDto)
                .toList();
    }

    @Override
    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    @Override
    public Long getNumberOfStudents() {
        return studentRepository.count();
    }

    @Override
    @Transactional
    public Optional<Student> deleteStudent(Long id) {

        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            logger.debug("StudentID {} not found for delete", id);
            return Optional.empty();
        }

        Student student = studentOpt.get();
        if (student.getAvatar() != null) {
            avatarService.deleteAvatar((student));
        }
        facultyRepository.findById(student.getFaculty().getId())
                .ifPresent(faculty -> facultyRepository.save(faculty.removeStudent(student)));

        studentRepository.deleteById(student.getId());
        logger.info("{} successfully deleted", student);
        return studentOpt;
    }


    @Override
    @Transactional   // Нужно вытагивать еще и аватары
    public List<StudentOutputDto> findStudentsByAge(int age, int upTo) {
        List<Student> students = upTo == -1L
                ? studentRepository.findByAge(age)
                : studentRepository.findByAgeBetween(age, upTo);

        logger.debug("Calling searchStudents with params: age={}, upTo={} returned student's ids: {}",
                age, upTo, students.stream().map(Student::getId).toList());

        return students.stream().map(studentMapper::toDto).toList();
    }

    @Override
    public Optional<FacultyOutputDto> getFaculty(Long studentId) {
        Optional<Faculty> facultyOpt = studentRepository.findById(studentId)
                .map(Student::getFaculty);
        if (facultyOpt.isEmpty()) {
//        Используется warn потому что у меня не предусмотрены студенты без факультетов
        logger.warn("StudentID {} doesn't have faculty", studentId);
        return Optional.empty();
        }
        return faculty.map(facultyMapper::toDto);

    }

    @Override
    public boolean setAvatar(Long studentId, MultipartFile avatarFile) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
                    logger.error("Filed to find studentID {} for set Avatar", studentId);
                    throw new EntityNotFoundException("Student with ID " + studentId + " not found");
                });

        boolean resultOfSaving = avatarService.downloadToLocal(student, avatarFile) && avatarService.downloadToDb(student, avatarFile);
        logger.info("Successful saving avatar and set its to {}", student);
        return resultOfSaving;

    }

    @Override
    public Optional<Avatar> getAvatar(Long studentId, boolean large) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
                    logger.error("Filed to search studentID {} in repo for get his avatar", studentId);
                    throw new EntityNotFoundException("Student with ID " + studentId + " not found");
                });


        if (student.getAvatar() == null) {
            logger.info("Avatar of studentID {} not found, but was requested", studentId);
            return Optional.empty();
        }
        Long avatarId = student.getAvatar().getId();

        /* В этом блоке try логика такая:
         *  - Исключение возникает только если аватар найден в БД, но локально файлы не удалось прочитать
         *  - Optional.empty() возвращается только если в БД не найдена запись
         *  - Наполненный Optional возвращается в случае полного успеха */
        try {
            Optional<Avatar> avatarOpt = large ? avatarService.getFromLocal(avatarId) : avatarService.findAvatar(avatarId);
            logger.info("avatar of studentID %s ".formatted(studentId) + (avatarOpt.isEmpty() ? "not found in DB" : "successfully getting"));
            return avatarOpt;

        } catch (Exception e) {   // Ловим здесь, потому что нам нужен studentId для фидбека исключения
            logger.error("Filed to read data image of StudentID {} from Local", studentId, e);
            throw new AvatarProcessingException("Unable to read avatar-data of student with id = " + studentId, e);
        }
    }

    private Faculty findFaculty(Long id) {
        return facultyRepository.findById(id).orElseThrow(() -> {
                    logger.error("Filed to found FacultyID {} for set to student", id);
                    throw new EntityNotFoundException("Faculty with ID " + id + " not found");
                });

    }
}
