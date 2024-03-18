package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.dto.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import com.evgeniyfedorchenko.hogwarts.mappers.AvatarMapper;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Service
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final AvatarMapper avatarMapper;

    @Value("${path.to.avatars.folder}")
    private Path avatarsDir;

    public AvatarServiceImpl(AvatarRepository avatarRepository,
                             StudentRepository studentRepository,
                             AvatarMapper avatarMapper) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        this.avatarMapper = avatarMapper;
    }

    @Override
    @Transactional
    public Optional<Avatar> findAvatar(Long avatarId) {
        return avatarRepository.findById(avatarId);
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
        String filePath = avatarsDir + "\\" + student + "." + getExtension(avatarFile.getOriginalFilename());
        Avatar avatar = avatarRepository.findByStudent_Id(student.getId())
                .orElseGet(Avatar::new);

        avatar.setFilePath(filePath);
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generatePreview(avatarFile.getBytes(), filePath));
        avatar.setStudent(student);

        return avatar;
    }

    private byte[] generatePreview(byte[] fullData, String filePath) throws IOException {

        try (ByteArrayInputStream bInStream = new ByteArrayInputStream(fullData);
             ByteArrayOutputStream bOutStream = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(bInStream);
            int previewWight = 100;
            int PreviewHeight = image.getHeight() / (image.getWidth() / previewWight);
            BufferedImage preview = new BufferedImage(previewWight, PreviewHeight, image.getType());

            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, previewWight, PreviewHeight, null);
            graphics.dispose();
            ImageIO.write(preview, getExtension(filePath), bOutStream);

            return bOutStream.toByteArray();
        }
    }

    @Override
    public boolean downloadToLocal(Student student, MultipartFile avatarFile) {
        try {
            if (!Files.exists(avatarsDir) || !Files.isDirectory(avatarsDir)) {
                Files.createDirectories(avatarsDir);
            }
            String fileName = student.toString();
            String extension = getExtension(avatarFile.getOriginalFilename());
            Path filePath = Path.of(avatarsDir + fileName + extension);

            deleteIfExists(fileName);

            Files.write(filePath, avatarFile.getBytes());
            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);   // Если проблемы с папками или доступом к ним
        }
    }

    private void deleteIfExists(String fileNameWithoutExtension) {

        if (Files.exists(avatarsDir)) {
            File[] listFiles = new File(avatarsDir.toUri()).listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file.getName().contains(fileNameWithoutExtension)) {
                        file.delete();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Optional<Avatar> getFromLocal(Long avatarId) throws IOException {
        Optional<Avatar> avatarOpt = findAvatar(avatarId);
        if (avatarOpt.isEmpty()) {
            return Optional.empty();
        }
        Avatar fromDb = avatarOpt.get();
        Avatar afr = new Avatar();

        afr.setData(Files.readAllBytes(Path.of(fromDb.getFilePath())));
        afr.setMediaType(fromDb.getMediaType());
        return Optional.of(afr);
    }

    @Override
    public List<AvatarDto> getAllAvatars(int pageNumber, int pageSize) {
        return avatarRepository.findAll(PageRequest.of(pageNumber - 1, pageSize))
                .get()
                .map(avatarMapper::toDto)
                .toList();
    }

    public void deleteAvatar(Student student) {
        avatarRepository.deleteById(student.getAvatar().getId());
        deleteIfExists(student.toString());
    }

    private String getExtension(String filePath) {
        return StringUtils.getFilenameExtension(filePath);
    }
}

