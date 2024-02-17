package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Student;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(Student student) {
        validateStudent(student);

        Student newStudent = new Student();
        newStudent.setName(student.getName());
        newStudent.setAge(student.getAge());

        return studentRepository.save(newStudent);
    }

    @Override
    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> updateStudent(Student student) {
        validateStudent(student);
        return studentRepository.findById(student.getId()).isPresent()
                ? Optional.of(studentRepository.save(student))
                : Optional.empty();
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
    public Optional<List<Student>> findStudentsByExactAge(int age) {
        List<Student> students = studentRepository.findByAge(age);
        return students.isEmpty() ? Optional.empty() : Optional.of(students);
    }

    @Override
    public Optional<List<Student>> findStudentsByAgeBetween(int min, int max) {
        if (max == 0) {
            max = Integer.MAX_VALUE;
        }
        List<Student> students = studentRepository.findByAgeBetween(min, max);
        return students.isEmpty() ? Optional.empty() : Optional.of(students);
    }

    private void validateStudent(Student student) {
        // TODO: 17.02.2024 не забыть добавить новые поля
        if (student.getName() == null || student.getAge() == 0) {
//             || student.getFacultyId() == null
            throw new IllegalStudentFieldsException("Any student's field cannot be equal null or zero or being empty");
        }
    }
}
