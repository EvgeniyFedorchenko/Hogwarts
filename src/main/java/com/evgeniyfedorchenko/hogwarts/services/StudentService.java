package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.controllers.SortOrder;
import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    StudentOutputDto createStudent(StudentInputDto studentInputDto);

    Optional<StudentOutputDto> findStudent(Long id);

    Optional<StudentOutputDto> updateStudent(Long id, StudentInputDto studentInputDto);

    Optional<Student> deleteStudent(Long id);

    List<StudentOutputDto> findStudentsByAge(int age, int upTo);

    Long getNumberOfStudents();

    Double getAverageAge();

    List<StudentOutputDto> searchStudents(String sortParam, SortOrder sortOrder, int pageNumber, int pageSize);

    Optional<FacultyOutputDto> getFaculty(Long studentId);

    boolean setAvatar(Long id, MultipartFile avatarFile);

    Optional<Avatar> getAvatar(Long id, boolean large);

    List<String> getStudentNamesStartsWith(String letter);

    Double getAverageAgeCalcByProgramMeans();
}
