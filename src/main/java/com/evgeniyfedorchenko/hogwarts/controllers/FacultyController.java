package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.dto.FacultyInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.services.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Faculties")
@RestController
@RequestMapping(path = "/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    @Operation(summary = "Creating a faculty")
    public FacultyOutputDto createFaculty(@RequestBody @Valid FacultyInputDto inputDto) {
        return facultyService.createFaculty(inputDto);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Get existing faculty")
    public ResponseEntity<FacultyOutputDto> getFaculty(@PathVariable
                                                       @Min(value = 1, message = "Id must be greater than 0") Long id) {
        return ResponseEntity.of(facultyService.findFaculty(id));
    }

    @GetMapping
    @Operation(summary = "Get faculties by color or part of name")
    public List<FacultyOutputDto> getFacultyByColorOrPartName(@RequestParam(required = false) Color color,
                                                              @RequestParam(required = false, defaultValue = "") String namePart) {
        return facultyService.findFacultyByColorOrPartName(color, namePart);
    }

    @GetMapping(path = "/{id}/students")
    @Operation(summary = "Get all students of faculty")
    public List<StudentOutputDto> getStudentsOfFaculty(@PathVariable
                                                       @Min(value = 1, message = "Id must be greater than 0") Long id) {
        return facultyService.findStudents(id);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Update existing faculty")
    public ResponseEntity<FacultyOutputDto> updateFaculty(@PathVariable
                                                          @Min(value = 1, message = "Id must be greater than 0") Long id,
                                                          @RequestBody @Valid FacultyInputDto inputDto) {
        return ResponseEntity.of(facultyService.updateFaculty(id, inputDto));
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete the existing faculty")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable @Min(value = 1, message = "Id must be greater than 0") Long id) {
        return ResponseEntity.of(facultyService.deleteFaculty(id));
    }

    @GetMapping(path = "/longest-name")
    @Operation(summary = "Get longest name of existing faculties")
    public ResponseEntity<String> getLongestFacultyName() {
        return ResponseEntity.of(facultyService.findLongestName());
    }
}
