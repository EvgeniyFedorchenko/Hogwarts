package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Students")
@RestController
@RequestMapping(path = "/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @Operation(summary = "Creating a student")
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get existing student")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.findStudent(id));
    }

    @GetMapping(params = "age")
    @Operation(summary = "Get students by exact ages")
    public List<Student> getStudentsByExactAge(@RequestParam int age) {
        return studentService.findStudentsByExactAge(age);
    }

    @GetMapping(params = {"minAge", "maxAge"})
    @Operation(summary = "Get students by range ages")
    public List<Student> getStudentByAgeBetween(@RequestParam int minAge, @RequestParam int maxAge) {
        return studentService.findStudentsByAgeBetween(minAge, maxAge);
    }

    @GetMapping(path = "/{id}/faculty")
    @Operation(summary = "Get faculty of existing student")
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.findFaculty(id));
    }

    @PutMapping
    @Operation(summary = "Update exist student")
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        return ResponseEntity.of(studentService.updateStudent(student));
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete exist student")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.deleteStudent(id));
    }
}
