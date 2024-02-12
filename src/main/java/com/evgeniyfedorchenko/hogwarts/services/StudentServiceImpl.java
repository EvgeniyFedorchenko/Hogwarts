package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Student;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(@NotEmpty Student student) {
        validateStudent(student);
        student.setId(0L);
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> getStudent(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> updateStudent(Long id, @NotNull Student student) {
        validateStudent(student);
        return studentRepository.findById(student.getId()).isPresent()
                ? Optional.of(studentRepository.save(student))
                : Optional.empty();
    }

    @Override
    public Optional<Student> deleteStudent(Long id) {

        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            studentRepository.deleteById(id);
            return studentOpt;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Student> getStudentWithAge(int age) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }

    private void validateStudent(Student student) {
        if (student.getName() == null || student.getAge() == 0 || student.getFacultyId() == null) {
            throw new IllegalStudentFieldsException("Any student's field cannot be equal null or zero or being empty");
        }
    }
}
