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
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return studentService.getStudent(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Student> getStudentsWithAge(@RequestParam int age) {
        return studentService.getStudentWithAge(age);
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
