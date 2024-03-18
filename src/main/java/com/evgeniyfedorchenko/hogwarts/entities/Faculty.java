package com.evgeniyfedorchenko.hogwarts.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private Color color;

    @Nullable
    @OneToMany(mappedBy = "faculty", fetch = FetchType.EAGER)
    private List<Student> students;

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

    public List<Student> getStudents() {
        return students == null ? new ArrayList<>() : students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    /**
     * @return - возвращает Faculty с обновленными студентами
     */
    public Faculty addStudent(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }
        student.setFaculty(this);
        this.students.add(student);
        return this;
    }

    /**
     * @return - возвращает Faculty с обновленными студентами
     */
    public Faculty removeStudent(Student student) {
        student.setFaculty(null);
        this.students.remove(student);
        return this;
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
        return Objects.equals(id, faculty.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Faculty %d - %s, clr, students: %s".formatted(id, name, students);
    }
}
