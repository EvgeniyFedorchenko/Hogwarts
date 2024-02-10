package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Student;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final Map<Long, Student> students;
    private Long countId = 0L;

    public StudentServiceImpl() {
        this.students = new HashMap<>();
    }

    @Override
    public Student createStudent(Student student) {
        student.setId(++countId);
        return students.put(student.getId(), student);
    }

    @Override
    public Optional<Student> getStudent(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public Optional<Student> updateStudent(Long id, Student student) {
        return (students.containsKey(id) && student != null)
                ? Optional.of(students.put(id, student))
                : Optional.empty();
    }

    @Override
    public Optional<Student> deleteStudent(Long id) {
        return (students.containsKey(id)) ? Optional.of(students.remove(id)) : Optional.empty();
    }
}
