package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.models.Student;
import com.evgeniyfedorchenko.hogwarts.services.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.findStudent(id));
    }

    @GetMapping(path = "/exact")
    public ResponseEntity<List<Student>> getStudentsByExactAge(@RequestParam int age) {
        return ResponseEntity.of(studentService.findStudentsByExactAge(age));
    }

    @GetMapping(path = "/range")
    public ResponseEntity<List<Student>> getStudentByAgeBetween(int min, int max) {
        // TODO: 17.02.2024 вернуть 404 если лист пустой
        return ResponseEntity.of(studentService.findStudentsByAgeBetween(min, max));
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        return ResponseEntity.of(studentService.updateStudent(student));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.deleteStudent(id));
    }
}
