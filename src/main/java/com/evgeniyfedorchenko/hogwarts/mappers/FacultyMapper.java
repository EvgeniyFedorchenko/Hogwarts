package com.evgeniyfedorchenko.hogwarts.mappers;

import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FacultyMapper {

    public FacultyOutputDto toDto(Faculty faculty) {
        FacultyOutputDto outputDto = new FacultyOutputDto();

        outputDto.setId(faculty.getId());
        outputDto.setName(faculty.getName());
        outputDto.setColor(faculty.getColor());

        List<Long> studentIds = faculty.getStudents().stream()
                .map(Student::getId)
                .toList();
        outputDto.setStudentIds(studentIds);

        return outputDto;
    }
}
