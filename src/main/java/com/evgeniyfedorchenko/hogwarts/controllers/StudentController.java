package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.dto.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.services.AvatarService;
import com.evgeniyfedorchenko.hogwarts.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Tag(name = "Students")
@RestController
@RequestMapping(StudentController.BASE_STUDENTS_URI)
public class StudentController {

    public static final String BASE_STUDENTS_URI = "/students";

    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService,
                             AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @PostMapping
    @Operation(summary = "Creating a student")
    public StudentOutputDto createStudent(@RequestBody @Valid StudentInputDto inputDto) {
        return studentService.createStudent(inputDto);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get existing student")
    public ResponseEntity<StudentOutputDto> getStudent(@PathVariable @Min(value = 1, message = "Id must be greater than 0") Long id) {
        return ResponseEntity.of(studentService.findStudent(id));
    }

    @GetMapping()
    @Operation(summary = "Student's search API")
    public List<StudentOutputDto> searchStudents(@RequestParam(required = false, defaultValue = "id") String sortParam,
                                                 @RequestParam(required = false, defaultValue = "ASC") SortOrder sortOrder,

                                                 @RequestParam(required = false, defaultValue = "1")
                                                 @Min(value = 1, message = "Number of page must be greater than 0") int pageNumber,

                                                 @RequestParam(required = false, defaultValue = "2147483648")
                                                 @Min(value = 1, message = "Size of page must be greater than 0") int pageSize) {
        return studentService.searchStudents(sortParam, sortOrder, pageNumber, pageSize);
    }

    @GetMapping(path = "/quantity")
    @Operation(summary = "Get the number of all students")
    public Long getNumberOfStudents() {
        return studentService.getNumberOfStudents();
    }

    @GetMapping(path = "/avg-age")
    @Operation(summary = "Get average age of all students")
    public Double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping(path = "/byAge")
    @Operation(summary = "Enter one value for an exact-match search and two values for a range search")
    public List<StudentOutputDto> getStudentByAge(@RequestParam
                                                  @Min(value = 16, message = "Age must be greater than 15") int age,
                                                  @RequestParam
                                                  @Min(value = 16, message = "Age must be greater than 15") int upTo) {
        return studentService.findStudentsByAge(age, upTo);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Update existing student")
    public ResponseEntity<StudentOutputDto> updateStudent(@PathVariable
                                                          @Min(value = 1, message = "Id must be greater than 0") Long id,
                                                          @RequestBody @Valid StudentInputDto inputDto) {
        return ResponseEntity.of(studentService.updateStudent(id, inputDto));
    }

    @GetMapping(path = "/{id}/faculty")
    @Operation(summary = "Get faculty of existing student")
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable
                                                       @Min(value = 1, message = "Id must be greater than 0") Long id) {
        return ResponseEntity.of(studentService.getFaculty(id));
    }

    /* В зависимости от флага boolean large выбираем откуда получить картинку:
       FALSE: получаем сжатое изображение (из БД). TRUE: получаем полное изображение (с диска) */
    @GetMapping(path = "/{id}/avatar")
    @Operation(summary = "Get avatar of student. Set \"large\" in \"true\" to get the best image resolution")
    public ResponseEntity<byte[]> getAvatar(@PathVariable @Min(value = 1, message = "Id must be greater than 0") Long id,
                                            @RequestParam(required = false, defaultValue = "false") boolean large) {
        return studentService.getAvatar(id, large)
                .map(this::setHeaders)
                .orElseGet(() -> ResponseEntity.of(Optional.empty()));
    }

    @PatchMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add avatar to student")
    public boolean setAvatar(@PathVariable @Min(value = 1, message = "Id must be greater than 0") Long id,
                             @RequestPart MultipartFile avatar) {
        return studentService.setAvatar(id, avatar);
    }

    @GetMapping(path = "/avatars")
    @Operation(summary = "Get all avatars")
    public List<AvatarDto> getAllAvatars(@RequestParam @Min(value = 1, message = "Number of page must be greater than 0") int pageNumber,
                                         @RequestParam @Min(value = 1, message = "Size of page must be greater than 0") int pageSize) {
        return avatarService.getAllAvatars(pageNumber, pageSize);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete existing student")
    public ResponseEntity<Student> deleteStudent(@PathVariable @Min(value = 1, message = "Id must be greater than 0") Long id) {
        return ResponseEntity.of(studentService.deleteStudent(id));
    }


    private ResponseEntity<byte[]> setHeaders(Avatar avatar) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentLength(avatar.getData().length)
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .body(avatar.getData());
    }

}