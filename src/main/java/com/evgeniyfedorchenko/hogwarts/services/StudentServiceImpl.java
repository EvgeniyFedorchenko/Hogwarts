package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Student createStudent(Student student) {
        validateStudent(student);

        Student newStudent = new Student();
        newStudent.setName(student.getName());
        newStudent.setAge(student.getAge());
        newStudent.setFaculty(student.getFaculty());

        return studentRepository.save(newStudent);
    }

    @Override
    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> updateStudent(Long id, Student student) {
        validateStudent(student);

        Optional<Student> studentById = studentRepository.findById(id);
        if (id <= 0L || studentById.isEmpty()) {
            return Optional.empty();
        }

        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            Student oldStudent = studentById.get();
            oldStudent.setName(student.getName());
            oldStudent.setAge(student.getAge());
            oldStudent.setFaculty(facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> new FacultyNotFoundException(student.getFaculty().getId())));

            return Optional.of(studentRepository.save(oldStudent));
        } else {
            return Optional.empty();
        }
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
    public List<Student> findStudentsByAgeBetween(int min, int max) {
        return max == -1L ? findStudentsByExactAge(min) : studentRepository.findByAgeBetween(min, max);
    }

    @Override
    public Optional<Faculty> findFaculty(Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.map(Student::getFaculty);
    }

    private void validateStudent(Student student) {
        if (student.getName() == null) {
            throw new IllegalStudentFieldsException(
                    "Student name cannot be null or empty", "name", student.getName());
        }
        if (student.getAge() == 0) {
            throw new IllegalStudentFieldsException(
                    "Student age cannot be equal zero", "age", String.valueOf(student.getAge()));
        }
    }
}
