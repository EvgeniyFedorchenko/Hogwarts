package com.evgeniyfedorchenko.hogwarts.controllers;

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
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        return ResponseEntity.of(facultyService.getFaculty(id));
        /*return facultyService.getFaculty(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());*/
    }

    @GetMapping
    public List<Faculty> getFacultyWithColor(@RequestParam Color color) {
        return facultyService.getFacultyWithColor(color);
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.of(facultyService.updateFaculty(faculty));
        /*return facultyService.updateFaculty(faculty)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());*/
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        return ResponseEntity.of(facultyService.deleteFaculty(id));
        /*return facultyService.deleteFaculty(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());*/
    }
}
