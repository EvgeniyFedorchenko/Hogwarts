package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AvatarService {

    Avatar findAvatar(Long id);

    boolean downloadToDb(Student student, MultipartFile avatarFile);

    boolean downloadToLocal(Student student, MultipartFile avatarFile);

    Avatar getFromLocal(Long avatarId) throws IOException;

    List<AvatarDto> getAllAvatars(int pageNumber, int pageSize);
}
