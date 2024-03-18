package com.evgeniyfedorchenko.hogwarts.mappers;

import com.evgeniyfedorchenko.hogwarts.controllers.StudentController;
import com.evgeniyfedorchenko.hogwarts.dto.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AvatarMapper {

    @Value("${server.port}")
    private int port;

    public AvatarDto toDto(Avatar avatar) {
        AvatarDto avatarDto = new AvatarDto();

        avatarDto.setId(avatar.getId());
        avatarDto.setMediaType(avatar.getMediaType());

        avatarDto.setPreviewUrl(generateUrlToAvatar(false, avatar.getStudent().getId()));
        avatarDto.setFullPictureUrl(generateUrlToAvatar(true, avatar.getStudent().getId()));

        avatarDto.setStudentName(avatar.getStudent().getName());
        avatarDto.setStudentId(avatar.getStudent().getId());

        return avatarDto;
    }


    protected String generateUrlToAvatar(boolean queryParamValue, Long studentId) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(StudentController.BASE_STUDENTS_URI)
                .pathSegment(String.valueOf(studentId), "avatar")
                .queryParam("large", queryParamValue)

                .build()
                .toUri()
                .toString();
    }
}
