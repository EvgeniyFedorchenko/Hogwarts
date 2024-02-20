package com.evgeniyfedorchenko.hogwarts.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private Color color;

    @JsonIgnore
    @OneToMany(mappedBy = "faculty")
    private Set<Student> students;

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

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
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
        return "Faculty %d: %s, clr, students: %s".formatted(id, name, students);
    }
}
