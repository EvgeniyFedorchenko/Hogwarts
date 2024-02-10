package com.evgeniyfedorchenko.hogwarts.models;

import java.util.Objects;

public class Faculty {

    private Long id;
    private String name;
    private Color color;

    public Faculty(Long id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id) && Objects.equals(name, faculty.name) && color == faculty.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
