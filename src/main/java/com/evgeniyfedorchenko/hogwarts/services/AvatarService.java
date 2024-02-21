package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface AvatarService {


    void downloadAvatar(Long studentId, MultipartFile avatar) throws IOException;

    Avatar findAvatar(Long id);
}
