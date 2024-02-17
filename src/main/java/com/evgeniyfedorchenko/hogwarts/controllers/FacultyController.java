package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.services.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        try {     // ???
            return ResponseEntity.ok(facultyService.createFaculty(faculty));
        } catch (IllegalFacultyFieldsException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        return ResponseEntity.of(facultyService.findFaculty(id));
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> getFacultyByColorOrPartName(@RequestParam(required = false) Color color,
                                                                     @RequestParam(required = false) String name) {
        return ResponseEntity.of(facultyService.findFacultyByColorOrPartName(color, name));
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.of(facultyService.updateFaculty(faculty));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        return ResponseEntity.of(facultyService.deleteFaculty(id));
    }
}
