/*
*
* 26.02.2024: Класс более не используется.
*             Эндпоинты для работы с аватарками перенесены в класс StudentController
*
*/

package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.services.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Deprecated
@Tag(name = "Avatars")
@RestController
@RequestMapping(path = "/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(path = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload avatar")
    public ResponseEntity<String> downloadAvatar(@PathVariable Long studentId, @RequestPart MultipartFile avatar) {
//        avatarService.downloadAvatar(studentId, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{id}/get-from-db")
    @Operation(summary = "Get avatar from database")
    public ResponseEntity<byte[]> uploadAvatar(@PathVariable Long id) {
        Avatar avatar = avatarService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.getData());
    }

    @GetMapping(path = "/{id}/get-from-local")
    @Operation(summary = "Get avatar from local storage")
    public void uploadAvatarFromLocal(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path), 1024);
             BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())
        ) {
            response.setContentType(avatar.getMediaType());
            response.setContentLength(avatar.getData().length);
            bis.transferTo(bos);
        }
    }
}
