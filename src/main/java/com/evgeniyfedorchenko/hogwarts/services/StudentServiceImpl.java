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
            return Optional.empty();
        }
        Student student = fillStudent(inputDto, studentById.get());
        studentRepository.save(student);

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
            return Optional.empty();
        }

        Student student = studentOpt.get();
        if (student.getAvatar() != null) {
            avatarService.deleteAvatar((student));
        }
        facultyRepository.findById(student.getFaculty().getId())
                .ifPresent(faculty -> facultyRepository.save(faculty.removeStudent(student)));

        studentRepository.deleteById(student.getId());
        return studentOpt;
    }


    @Override
    @Transactional   // Нужно вытагивать еще и аватары
    public List<StudentOutputDto> findStudentsByAge(int age, int upTo) {
        List<Student> students = upTo == -1L
                ? studentRepository.findByAge(age)
                : studentRepository.findByAgeBetween(age, upTo);

        return students.stream().map(studentMapper::toDto).toList();
    }

    @Override
    public Optional<FacultyOutputDto> getFaculty(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .map(facultyMapper::toDto);
    }

    @Override
    public boolean setAvatar(Long studentId, MultipartFile avatarFile) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with ID " + studentId + " not found for set Avatar"));

        return avatarService.downloadToLocal(student, avatarFile) && avatarService.downloadToDb(student, avatarFile);
    }

    @Override
    public Optional<Avatar> getAvatar(Long studentId, boolean large) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with ID " + studentId + " not found for set Avatar"));

        if (student.getAvatar() == null) {
            return Optional.empty();
        }
        Long avatarId = student.getAvatar().getId();

        try {
            return large ? avatarService.getFromLocal(avatarId) : avatarService.findAvatar(avatarId);
        } catch (Exception e) {   // Ловим здесь, потому что нам нужен studentId для фидбека исключения
            throw new AvatarProcessingException("Unable to read avatar-data of student with id = " + studentId, e);
        }
    }

    private Faculty findFaculty(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FacultyId " + id + " not found"));
    }
}
