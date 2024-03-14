package com.evgeniyfedorchenko.hogwarts.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "avatars")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath;   // TODO: 06.03.2024 Мб сделать поле типа Path ?
    private String mediaType;   // TODO: 06.03.2024 Мб сделать поле типа MediaType ?
    
    @JsonIgnore
    @Lob
    @Column(columnDefinition = "oid")
    private byte[] data;

    @JsonIgnore
    @OneToOne(mappedBy = "avatar")
    private Student student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object otherAvatar) {
        if (this == otherAvatar) {
            return true;
        }
        if (otherAvatar == null || getClass() != otherAvatar.getClass()) {
            return false;
        }
        Avatar avatar = (Avatar) otherAvatar;
        return id.equals(avatar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Avatar %d of student %d".formatted(id, student.getId());
    }
}
