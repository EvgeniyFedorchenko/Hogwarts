package com.evgeniyfedorchenko.hogwarts.dto;

import com.evgeniyfedorchenko.hogwarts.entities.Color;

import java.util.List;

public class FacultyOutputDto {

    private Long id;
    private String name;
    private Color color;
    private List<Long> studentIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}
