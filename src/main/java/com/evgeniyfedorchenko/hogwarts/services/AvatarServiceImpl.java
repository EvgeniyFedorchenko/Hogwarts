package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.util.StringUtils.getFilenameExtension;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;

    @Value("${path.to.avatars.folder}")
    private Path avatarsDir;
    private final String fileName = avatarsDir + "%s.%s";

    public AvatarServiceImpl(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    @Transactional
    public Avatar findAvatar(Long id) {
        return avatarRepository.findById(id).orElseThrow(() ->
                new AvatarNotFoundException("Avatar with ID " + id + "not found", "Avatar", String.valueOf(id)));
    }

    @Override
    @Transactional
    public boolean downloadToDb(Student student, MultipartFile avatarFile) {

        byte[] data;
        try {
            data = avatarFile.getBytes();
        } catch (IOException e) {
            throw new AvatarProcessingException(e);   // Если проблемы с изображением
        }
//        String fileName = avatarsDir + student.toString() + "." + getFilenameExtension(avatarFile.getOriginalFilename());

        Avatar avatar = avatarRepository.findByStudent_Id(student.getId()).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(fileName
                .formatted(student.toString(), getFilenameExtension(avatarFile.getOriginalFilename())));
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(data);

        avatarRepository.save(avatar);
        student.setAvatar(avatar);
        return true;
    }

    @Override
    public boolean downloadToLocal(Student student, MultipartFile avatarFile) {
        try {
            if (!Files.exists(avatarsDir) || !Files.isDirectory(avatarsDir)) {
                Files.createDirectories(avatarsDir);
            }
            Path path = Path.of(fileName
                    .formatted(student.toString(), getFilenameExtension(avatarFile.getOriginalFilename())));

//            Здесь будет сжатие изображения
//            Path path = Path.of(avatarsDir + student.toString() + "." + getFilenameExtension(avatarFile.getOriginalFilename());

            Files.write(path, avatarFile.getBytes());
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);   // Если проблемы с папками или доступом к ним
        }
    }

    @Override
    public Avatar getFromLocal(Long id) {
        Avatar avatar = findAvatar(id);
        Avatar afr = new Avatar();
        byte[] data;

        try {
            data = Files.readAllBytes(Path.of(avatar.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        afr.setData(data);
        afr.setMediaType(avatar.getMediaType());
        return afr;
    }
}
