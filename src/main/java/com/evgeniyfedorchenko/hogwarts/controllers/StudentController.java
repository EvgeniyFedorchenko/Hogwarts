package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping(params = {"age", "upTo"})
    @Operation(summary = "Enter one value for an exact-match search and two values for a range search")
    public List<Student> getStudentByAgeBetween(@RequestParam int age,
                                                @RequestParam(required = false, defaultValue = "-1") int upTo) {
        return studentService.findStudentsByAgeBetween(age, upTo);
    }


    @GetMapping(path = "/{id}/faculty")
    @Operation(summary = "Get faculty of existing student")
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.findFaculty(id));
    }


    @PutMapping(path = "/{id}")
    @Operation(summary = "Update existing student")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return ResponseEntity.of(studentService.updateStudent(id, student));
    }


    @PatchMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add avatar to student")
    public boolean setAvatar(@PathVariable Long id, @RequestPart MultipartFile avatar) {
        return studentService.setAvatar(id, avatar);
    }


    /* В зависимости от флага large выбирает откуда получить картинку:
     *  - на диске хранится полноценное изображение, получаем его если large = true
     *  - в БД хранится уменьшенная копия - превью, получаем её, если large = false
     *  (пока что везде хранится одинаковая картинка, но я позже реализую это) */
    @GetMapping(path = "/{id}/avatar")
    @Operation(summary = "Get avatar of student. Set \"large\" in \"true\" to get the best image resolution")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id,
                                            @RequestParam(required = false, defaultValue = "false") boolean large) {
        return transform(studentService.getAvatar(id, large));
    }


    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete existing student")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.deleteStudent(id));
    }

    private ResponseEntity<byte[]> transform(Avatar avatar) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentLength(avatar.getData().length)
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getData());
    }
}
