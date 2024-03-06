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
    private final FacultyService facultyService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              FacultyRepository facultyRepository,
                              AvatarService avatarService,
                              FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarService = avatarService;
        this.facultyService = facultyService;
    }

    @Override
    public Student createStudent(Student student) {
        validateStudentsFields(student);
        checkExistenceFaculty(student);

        Student newStudent = new Student();
        newStudent.setName(student.getName());
        newStudent.setAge(student.getAge());

        Faculty supposedFaculty = student.getFaculty();
        if (facultyRepository.findById(supposedFaculty.getId()).isPresent()) {
            newStudent.setFaculty(supposedFaculty);
        }

        Student savedStudent = studentRepository.save(newStudent);
        enrollStudentInFaculty(savedStudent);   // Говорим факультету, что у него новый студент
        return savedStudent;
    }

    private void enrollStudentInFaculty(Student student) {
        Faculty faculty = student.getFaculty();
        List<Student> newListStudents = faculty.getStudents();
        newListStudents.add(student);
        faculty.setStudents(newListStudents);

        facultyService.updateFaculty(faculty.getId(), faculty);
    }


    @Override
    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> updateStudent(Long id, Student student) {
        validateStudentsFields(student);

        Optional<Student> studentById = studentRepository.findById(id);
        if (id <= 0L || studentById.isEmpty()) {
            return Optional.empty();
        }

        checkExistenceFaculty(student);   // Валидация факультета

        Student oldStudent = studentById.get();
        oldStudent.setName(student.getName());
        oldStudent.setAge(student.getAge());
        oldStudent.setFaculty(student.getFaculty());

        // Если студент меняет факультет - из старого его выгоняем (просто обновляем старый факультет)
        if (!oldStudent.getFaculty().equals(student.getFaculty())) {
            enrollStudentInFaculty(student);
            expelStudentFromFaculty(oldStudent);
        }
        if (student.getAvatar() != null) {
            oldStudent.setAvatar(student.getAvatar());

        }
        return Optional.of(studentRepository.save(oldStudent));

    }

    private void expelStudentFromFaculty(Student oldStudent) {
        Faculty oldFaculty = oldStudent.getFaculty();
        List<Student> students = oldFaculty.getStudents();
        students.remove(oldStudent);

        facultyService.updateFaculty(oldFaculty.getId(), oldFaculty);
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
    public List<Student> findStudentsByExactAge(int age) {
        return studentRepository.findByAge(age);
    }

    @Override
    public List<Student> findStudentsByAge(int min, int max) {
        return max == -1L ? findStudentsByExactAge(min) : studentRepository.findByAgeBetween(min, max);
    }

    @Override
    public Optional<Faculty> findFaculty(Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.map(Student::getFaculty);
    }

    @Override
    public boolean setAvatar(Long studentId, MultipartFile avatarFile) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student with ID " + studentId + "not found", "id", String.valueOf(studentId)));

        return  avatarService.downloadToLocal(student, avatarFile) && avatarService.downloadToDb(student, avatarFile);
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
        }
        if (student.getAge() <= 0) {
            throw new IllegalStudentFieldsException(
                    "Student age cannot be equal zero", "age", String.valueOf(student.getAge()));
        }
    }

    private void checkExistenceFaculty(Student student) {

        if (student.getFaculty() == null || student.getFaculty().getId() == null) {
            throw new IllegalStudentFieldsException("Faculty or its ID must not be null", "faculty", "empty");

        }
        Long facultyId = student.getFaculty().getId();
        facultyRepository.findById(facultyId).orElseThrow(() ->
                new FacultyNotFoundException("Faculty with ID " + facultyId + "not found",
                        "id",
                        String.valueOf(facultyId)));
    }
}
