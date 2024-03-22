package com.evgeniyfedorchenko.hogwarts.dto;

import com.evgeniyfedorchenko.hogwarts.entities.Color;

import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {
            return "Faculty %d - %s, clr, students: %s".formatted(id, name, studentIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FacultyOutputDto outputDto = (FacultyOutputDto) o;
        return id.equals(outputDto.id)
               && name.equals(outputDto.name)
               && color == outputDto.color
               && Objects.equals(studentIds, outputDto.studentIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, studentIds);
    }
}
