package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.AvatarNotFoundException;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.util.StringUtils.getFilenameExtension;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private Path avatarsDir;

    @Value("${server.port}")
    private String port;

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
        Avatar avatar;
        try {
            avatar = fillAvatar(student, avatarFile);
        } catch (IOException e) {
            throw new AvatarProcessingException("Unable to read avatar-data of student with id = " + student.getId(), e);
        }

        Avatar savedAvatar = avatarRepository.save(avatar);
        student.setAvatar(savedAvatar);
        studentRepository.save(student);
        return true;
    }

    private Avatar fillAvatar(Student student, MultipartFile avatarFile) throws IOException {

        Avatar avatar = avatarRepository.findByStudent_Id(student.getId()).orElseGet(Avatar::new);
        avatar.setFilePath(avatarsDir + "/" + student + "." + getFilenameExtension(avatarFile.getOriginalFilename()));
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());
        avatar.setStudent(student);

        return avatar;
    }

    @Override
    public boolean downloadToLocal(Student student, MultipartFile avatarFile) {
        try {
            if (!Files.exists(avatarsDir) || !Files.isDirectory(avatarsDir)) {
                Files.createDirectories(avatarsDir);
            }
            Path path = Path.of(avatarsDir + "/" + student.toString() + "." + getFilenameExtension(avatarFile.getOriginalFilename()));

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

        afr.setData(Files.readAllBytes(Path.of(avatar.getFilePath())));
        afr.setMediaType(avatar.getMediaType());
        return afr;
    }

    @Override
    public List<AvatarDto> getAllAvatars(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        List<Avatar> avatars = avatarRepository.findAll(pageRequest).getContent();
        return avatars.stream()
                .map(avatar -> new AvatarDto(
                        avatar.getId(),
                        avatar.getFilePath(),
                        avatar.getMediaType(),
                        "http://localhost:%s/students/%d/avatar".formatted(port, avatar.getStudent().getId()),
                        avatar.getStudent().getId(),
                        avatar.getStudent().getName())
                ).toList();
    }
}

