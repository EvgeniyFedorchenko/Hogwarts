package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.dto.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AvatarService {

    Optional<Avatar> findAvatar(Long id);

    boolean downloadToDb(Student student, MultipartFile avatarFile);

    boolean downloadToLocal(Student student, MultipartFile avatarFile);

    Optional<Avatar> getFromLocal(Long avatarId) throws IOException;

    List<AvatarDto> getAllAvatars(int pageNumber, int pageSize);

    void deleteAvatar(Student student);
}
