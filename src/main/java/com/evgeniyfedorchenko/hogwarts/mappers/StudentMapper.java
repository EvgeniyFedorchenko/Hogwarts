package com.evgeniyfedorchenko.hogwarts.mappers;

import com.evgeniyfedorchenko.hogwarts.dto.StudentInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    @Autowired
    private AvatarMapper avatarMapper;

    public StudentOutputDto toDto(Student student) {
        StudentOutputDto studentOutputDto = new StudentOutputDto();

        studentOutputDto.setId(student.getId());
        studentOutputDto.setName(student.getName());
        studentOutputDto.setAge(student.getAge());
        studentOutputDto.setFacultyId(student.getFaculty().getId());

        if (student.getAvatar() != null) {
            studentOutputDto.setAvatarUrl(avatarMapper.generateUrlToAvatar(true, student.getId()));
        } else {
            studentOutputDto.setAvatarUrl(null);
        }

        return studentOutputDto;
    }
}
