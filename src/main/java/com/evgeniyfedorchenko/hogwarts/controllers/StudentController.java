package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.services.AvatarService;
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
    private final AvatarService avatarService;

    public StudentController(StudentService studentService,
                             AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
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


    @GetMapping()
    @Operation(summary = "Enter one value for an exact-match search and two values for a range search")
    public List<Student> getStudentByAge(@RequestParam int age,
                                         @RequestParam(required = false, defaultValue = "-1") int upTo) {
        return studentService.findStudentsByAge(age, upTo);
    }


    @GetMapping(path = "/{id}/faculty")
    @Operation(summary = "Get faculty of existing student")
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.getFaculty(id));
    }

    @GetMapping(path = "/quantity")
    @Operation(summary = "Get the number of all students")
    public Long getNumberOfStudents() {
        return studentService.getNumberOfStudents();
    }

    @GetMapping(path = "/avg-age")
    @Operation(summary = "Get average age of all students")
    public Integer getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping(path = "/last/{quantity}")
    @Operation(summary = "Get the last \"quantity\" students")
    public List<Student> getLastStudents(@PathVariable int quantity) {
        return studentService.findLastStudents(quantity);
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


    /* В зависимости от флага large выбираем откуда получить картинку:
     *  - на диске хранится полноценное изображение, получаем его если large = true
     *  - в БД хранится уменьшенная копия - превью, получаем её, если large = false
     *  (пока что везде хранится одинаковая картинка, но я позже реализую это) */
    @GetMapping(path = "/{id}/avatar")
    @Operation(summary = "Get avatar of student. Set \"large\" in \"true\" to get the best image resolution")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id,
                                            @RequestParam(required = false, defaultValue = "false") boolean large) {
        return setHeaders(studentService.getAvatar(id, large));
    }

    @GetMapping(path = "/avatars")
    @Operation(summary = "Get all avatars")
    public List<AvatarDto> getAllAvatars(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return avatarService.getAllAvatars(pageNumber, pageSize);
    }


    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete existing student")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.of(studentService.deleteStudent(id));
    }


    private ResponseEntity<byte[]> setHeaders(Avatar avatar) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentLength(avatar.getData().length)
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getData());
    }

    /*
     *           URL               PARAMS
     * /students
     *     POST /               |
     *     GET /{id}            |
     *     GET /                | age, upTo
     *     GET /{id}/faculty    |
     *     PATCH /{id}/avatar   |
     *   * GET /quantity        |
     *   * GET /avg-age         | quantity
     *   * GET /last/{quantity} |
     *     PUT /{id}            |
     *     GET /{id}/avatar     | large
     *     DELETE /{id}         |
     *   * GET /avatars         | pageNumber, pageSize
     *
     *   * - new
     *
     * */
}
