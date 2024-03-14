package com.evgeniyfedorchenko.hogwarts.entities;

public class AvatarDto {

    private Long id;
    private String filePath;
    private String mediaType;
    private String data;
    private Long studentId;
    private String studentName;

    public AvatarDto(Long id, String filePath, String mediaType, String data, Long studentId, String studentName) {
        this.id = id;
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.data = data;
        this.studentId = studentId;
        this.studentName = studentName;
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
