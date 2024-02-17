package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.InvalidStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.StudentNotFoundException;
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
    public Optional<Student> updateStudent(Student student) {
        validateStudent(student);
        Optional<Student> studentOpt = studentRepository.findById(student.getId());
        if (studentOpt.isPresent()) {
            Student oldStudent = studentOpt.get();
            oldStudent.setName(student.getName());
            oldStudent.setAge(student.getAge());
            oldStudent.setFaculty(student.getFaculty());

            return Optional.of(studentRepository.save(oldStudent));
        }
        else {
            throw new StudentNotFoundException(student.getId());
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
        if (max == 0) {
            max = Integer.MAX_VALUE;
        }
        return studentRepository.findByAgeBetween(min, max);
    }

    @Override
    public Optional<Faculty> findFaculty(Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.map(Student::getFaculty);
    }

    private void validateStudent(Student student) {
        if (student.getName() == null) {
            throw new InvalidStudentFieldsException(
                    "Student name cannot be null or empty", "name", student.getName());
        }
        if (student.getAge() == 0) {
            throw new InvalidStudentFieldsException(
                    "Student age cannot be equal zero", "age", String.valueOf(student.getAge()));
        }
        if (student.getFaculty() != null && student.getFaculty().getId() != null) {
            facultyRepository.findById(student.getFaculty().getId())
                    .orElseThrow(() -> new FacultyNotFoundException(student.getFaculty().getId()));
        }
    }
}
