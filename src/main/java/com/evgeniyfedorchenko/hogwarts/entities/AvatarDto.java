package com.evgeniyfedorchenko.hogwarts.entities;

import org.springframework.beans.factory.annotation.Value;

public class AvatarDto {

    private Long id;
    private String filePath;
    private String mediaType;
    private String data;   // Здесь храним URL
    private Long studentId;
    private String studentName;

    @Value("${server.port}")
    private String port;

    public AvatarDto(Long id, String filePath, String mediaType,
                     String data, Long studentId, String studentName) {
        this.id = id;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.data = data;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public AvatarDto(Avatar avatar) {
        this.id = avatar.getId();
        this.filePath = avatar.getFilePath();
        this.mediaType = avatar.getMediaType();
        this.data = "http://localhost:%s/students/%d/avatar".formatted(port, avatar.getStudent().getId());
        this.studentId = avatar.getStudent().getId();
        this.studentName = avatar.getStudent().getName();
    }

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
