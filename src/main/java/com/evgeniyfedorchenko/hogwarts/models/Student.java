package com.evgeniyfedorchenko.hogwarts.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private int age;
    private Long facultyId;

    public Student() {

    }

    public Student(Long id, String name, int age, Long facultyId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.facultyId = facultyId;
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

    @Override
    public boolean equals(Object otherStudent) {     /* Каждый студент имеет свой УНИКАЛЬНЫЙ id */
        if (this == otherStudent) {
            return true;
        }
        if (otherStudent == null || getClass() != otherStudent.getClass()) {
            return false;
        }
        Student student = (Student) otherStudent;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
