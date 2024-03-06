package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.AvatarNotFoundException;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
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
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private Path avatarsDir;

    public AvatarServiceImpl(AvatarRepository avatarRepository,
                             StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public Avatar findAvatar(Long avatarId) {
        return avatarRepository
                .findById(avatarId)
                .orElseThrow(() -> new AvatarNotFoundException(
                        "Avatar with ID " + avatarId + "not found",
                        "Avatar",
                        String.valueOf(avatarId)));
    }

    @Override
    @Transactional
    public boolean downloadToDb(Student student, MultipartFile avatarFile) {

        byte[] data;
        try {
            data = avatarFile.getBytes();
        } catch (IOException e) {     // Если проблемы с изображением
            throw new AvatarProcessingException("Unable to read avatar-data of student with id = " + student.getId(), e);
        }

        Avatar avatar = avatarRepository.findByStudent_Id(student.getId()).orElseGet(Avatar::new);
        avatar.setStudent(student);

        avatar.setFilePath(avatarsDir + "\\" + student + "." + getFilenameExtension(avatarFile.getOriginalFilename()));

        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(data);

        avatarRepository.save(avatar);

        student.setAvatar(avatar);
        studentRepository.save(student);
        return true;
    }

    @Override
    public boolean downloadToLocal(Student student, MultipartFile avatarFile) {
        try {
            if (!Files.exists(avatarsDir) || !Files.isDirectory(avatarsDir)) {
                Files.createDirectories(avatarsDir);
            }
            Path path = Path.of(avatarsDir + "\\" + student.toString() + "." + getFilenameExtension(avatarFile.getOriginalFilename()));

//            Здесь будет сжатие изображения

            Files.write(path, avatarFile.getBytes());
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);   // Если проблемы с папками или доступом к ним
        }
    }

    @Override
    public Avatar getFromLocal(Long avatarId) throws IOException {
        Avatar avatar = findAvatar(avatarId);
        Avatar afr = new Avatar();
        byte[] data;

        data = Files.readAllBytes(Path.of(avatar.getFilePath()));

        afr.setData(data);
        afr.setMediaType(avatar.getMediaType());
        return afr;
    }
}
