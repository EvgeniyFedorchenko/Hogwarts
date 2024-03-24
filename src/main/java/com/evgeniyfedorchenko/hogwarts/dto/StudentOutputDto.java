package com.evgeniyfedorchenko.hogwarts.dto;

import java.util.Objects;

public class StudentOutputDto {

    private Long id;
    private String name;
    private int age;
    private Long facultyId;
    private String avatarUrl;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "StudentOutputDto:ID%d-%s,%dy.o.(%d)".formatted(id, name, age, facultyId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StudentOutputDto that = (StudentOutputDto) o;
        return age == that.age
               && id.equals(that.id)
               && name.equals(that.name)
               && facultyId.equals(that.facultyId)
               && Objects.equals(avatarUrl, that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, facultyId, avatarUrl);
    }
}
