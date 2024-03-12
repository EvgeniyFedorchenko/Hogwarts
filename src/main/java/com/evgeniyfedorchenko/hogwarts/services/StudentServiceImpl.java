package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.FacultyNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.IllegalStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.StudentNotFoundException;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarService avatarService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              FacultyRepository facultyRepository,
                              AvatarService avatarService) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarService = avatarService;
    }

    @Override
    public Student createStudent(Student student) {
        validateStudentsFields(student);
        validateFacultyFields(student.getFaculty());

        Faculty findedFaculty = findFaculty(student.getFaculty().getId());
        Student savedStudent = studentRepository.save(fillStudent(student, new Student()));

        findedFaculty.addStudent(savedStudent);
        facultyRepository.save(findedFaculty);

        return savedStudent;
    }

    @Override
    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> updateStudent(Long id, Student student) {
        if (id <= 0L) {
            return Optional.empty();
        }

        validateStudentsFields(student);
        Optional<Student> studentById = studentRepository.findById(id);
        if (studentById.isEmpty()) {
            return Optional.empty();
        }
        validateFacultyFields(student.getFaculty());
        return Optional.of(studentRepository.save(fillStudent(student, studentById.get())));
    }

    private Student fillStudent(Student src, Student dest) {

        Faculty findedFaculty = findFaculty(src.getFaculty().getId());

        dest.setName(src.getName());
        dest.setAge(src.getAge());
        dest.setAvatar(src.getAvatar());

//        Если меняется факультет - обновляем списки студентов у нового и старого факультета
//        dest.getFaculty() может быть null, если мы пришли сюда из метода create()
        if (dest.getFaculty() != null && !dest.getFaculty().equals(findedFaculty)) {
            facultyRepository.save(dest.getFaculty().removeStudent(dest));
            facultyRepository.save(findedFaculty.addStudent(dest));
        } else {
            dest.setFaculty(findedFaculty);
        }
        return dest;
    }

    @Override
    public Optional<Student> deleteStudent(Long id) {

        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            studentRepository.delete(studentOpt.get());
            return studentOpt;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Student> findStudentsByAge(int age, int upTo) {
        return upTo == -1L ? studentRepository.findByAge(age) : studentRepository.findByAgeBetween(age, upTo);
    }

    @Override
    public Optional<Faculty> getFaculty(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getFaculty);
    }

    @Override
    public boolean setAvatar(Long studentId, MultipartFile avatarFile) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student with ID " + studentId + "not found", "id", String.valueOf(studentId)));

        return avatarService.downloadToLocal(student, avatarFile) && avatarService.downloadToDb(student, avatarFile);
    }

    @Override
    public Avatar getAvatar(Long studentId, boolean large) {
        Long avatarId = studentRepository.findById(studentId).orElseThrow(() ->
                        new StudentNotFoundException("Student with ID " + studentId + "not found", "id", String.valueOf(studentId)))
                .getAvatar()
                .getId();

        try {
            return large ? avatarService.getFromLocal(avatarId) : avatarService.findAvatar(avatarId);
        } catch (Exception e) {   // Ловим здесь, потому что нам нужен studentId для фидбека исключения
            throw new AvatarProcessingException("Unable to read avatar-data of student with id = " + studentId, e);
        }
    }

    private void validateStudentsFields(Student student) {
        if (student.getName() == null) {
            throw new IllegalStudentFieldsException(
                    "Student name cannot be null or empty", "name", student.getName());
        } else if (student.getAge() <= 0) {
            throw new IllegalStudentFieldsException(
                    "Student age cannot be equal zero", "age", String.valueOf(student.getAge()));
        }
    }

    private void validateFacultyFields(Faculty faculty) {
        if (faculty == null || faculty.getId() == null) {
            throw new IllegalStudentFieldsException("Faculty or its ID must not be null", "faculty", "empty");
        }
    }

    private Faculty findFaculty(Long facultyId) {
        return facultyRepository.findById(facultyId).orElseThrow(() ->
                new FacultyNotFoundException(
                        "Faculty with ID " + facultyId + "not found",
                        "id",
                        String.valueOf(facultyId)
                ));
    }
}
