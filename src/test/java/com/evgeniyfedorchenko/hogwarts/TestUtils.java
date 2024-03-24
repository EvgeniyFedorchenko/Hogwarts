package com.evgeniyfedorchenko.hogwarts;

import com.evgeniyfedorchenko.hogwarts.dto.StudentInputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;

@Component
public class TestUtils {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AvatarRepository avatarRepository;


    public void linkStudentAndAvatar(Student student, Avatar avatar) throws IOException {

        Files.createDirectories(testResourceDir);
        Files.write(
                Path.of(sentResourcePath().formatted(student)),
                Files.readAllBytes(testResourcePath()));

        avatar.setId(null);
        avatar.setFilePath(avatar.getFilePath().formatted(student));
        avatar.setStudent(student);
        Avatar savedAvatar = avatarRepository.save(avatar);

        student.setAvatar(savedAvatar);
        studentRepository.save(student);
    }

    public StudentInputDto toInputDto(Student student) {
        StudentInputDto inputDto = new StudentInputDto();

        inputDto.setName(student.getName());
        inputDto.setAge(student.getAge());
        inputDto.setFacultyId(student.getFaculty().getId());

        return inputDto;
    }

    public RestTemplate patchedRestTemplate(TestRestTemplate testRestTemplate) {
        RestTemplate patchRestTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return patchRestTemplate;
    }
}
