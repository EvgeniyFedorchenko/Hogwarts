package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Student;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        students.put(student.getId(), student);
        return student;
    }

    @Override
    public Optional<Student> getStudent(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public Optional<Student> updateStudent(Long id, Student student) {
        if (students.containsKey(id) && student != null) {
            Student old = students.get(id);
            students.replace(id, student);
            return Optional.of(old);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Student> deleteStudent(Long id) {
        return (students.containsKey(id)) ? Optional.of(students.remove(id)) : Optional.empty();
    }

    @Override
    public List<Student> getStudentWithAge(int age) {
        return students.values().stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }
}
