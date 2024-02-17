package com.evgeniyfedorchenko.hogwarts.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Color color;

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
    public boolean equals(Object otherFaculty) {
        if (this == otherFaculty) {
            return true;
        }
        if (otherFaculty == null || getClass() != otherFaculty.getClass()) {
            return false;
        }
        Faculty faculty = (Faculty) otherFaculty;
        return Objects.equals(id, faculty.id)
               && Objects.equals(name, faculty.name)
               && color == faculty.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Faculty %d: %s, clr".formatted(id, name);
    }
}
