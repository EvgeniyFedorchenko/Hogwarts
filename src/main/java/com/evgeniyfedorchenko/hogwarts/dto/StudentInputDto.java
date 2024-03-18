package com.evgeniyfedorchenko.hogwarts.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StudentInputDto {

    @NotNull(message = "Student name cannot be empty")
    private String name;

    @Min(value = 16, message = "Student's age must be greater than 15")
    private int age;

    @Min(value = 1, message = "FacultyId must be greater than 0")
    private long facultyId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }
}
