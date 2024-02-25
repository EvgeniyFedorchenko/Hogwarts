package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.StudentNotFoundException;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;

    public AvatarServiceImpl(AvatarRepository avatarRepository,
                             StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void downloadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new StudentNotFoundException("Student with ID " + studentId + "not found",
                                "id", String.valueOf(studentId)));
        Path filePath = Path.of(avatarsDir,
                studentId + "-" + student.getName() + getExtension(avatarFile.getOriginalFilename()));

        downloadToLocal(avatarFile, filePath);
        downloadToDb(avatarFile, filePath, student);
    }

    @Override
    @Transactional
    public Avatar findAvatar(Long id) {
        return avatarRepository.findById(id).orElseThrow(() ->
                new AvatarNotFoundException("Avatar with ID " + id + "not found", "Avatar", String.valueOf(id)));
    }

    private void downloadToLocal(MultipartFile avatarFile, Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (BufferedInputStream bis = new BufferedInputStream(avatarFile.getInputStream(), 1024);
             BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath, CREATE_NEW), 1024)
        ) {
            bis.transferTo(bos);
        }
    }

    private void downloadToDb(MultipartFile avatarFile, Path filePath, Student student) throws IOException {

        Avatar avatar = avatarRepository.findByStudent_Id(student.getId()).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());
        avatarRepository.save(avatar);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


}
