package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.FacultyNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.IllegalStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import com.evgeniyfedorchenko.hogwarts.services.AvatarServiceImpl;
import com.evgeniyfedorchenko.hogwarts.services.FacultyServiceImpl;
import com.evgeniyfedorchenko.hogwarts.services.StudentServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyRepository facultyRepositoryMock;
    @MockBean
    private StudentRepository studentRepositoryMock;
    @MockBean
    private AvatarRepository avatarRepositoryMock;

    @SpyBean
    private FacultyServiceImpl facultyServiceImplSpy;
    @SpyBean
    private AvatarServiceImpl avatarServiceImplSpy;
    @SpyBean
    private StudentServiceImpl studentServiceImplSpy;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    public void beforeEach() {
        testConstantsInitialisation();
    }

    @Test
    void createStudentPositiveTest() throws Exception {
        Student targetStudent = STUDENT_1;
        targetStudent.setFaculty(FACULTY_1);

        when(facultyRepositoryMock.findById(FACULTY_1.getId())).thenReturn(Optional.of(FACULTY_1));
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(STUDENT_1);
        doReturn(Optional.of(FACULTY_1)).when(facultyServiceImplSpy).updateFaculty(anyLong(), any(Faculty.class));

        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(targetStudent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(targetStudent.getName()))
                .andExpect(jsonPath("$.age").value(targetStudent.getAge()))
                .andExpect(jsonPath("$.faculty.name").value(targetStudent.getFaculty().getName()))
                .andExpect(jsonPath("$.faculty.id").value(targetStudent.getFaculty().getId()));
    }

    @Test
    void createStudentWithIllegalFieldsNegativeTest() throws Exception {
        STUDENT_1.setName(null);
        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(STUDENT_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IllegalStudentFieldsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Value (.*?) of parameter (.*?) of student is invalid"));

        STUDENT_2.setAge(0);

        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(STUDENT_2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IllegalStudentFieldsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Value (.*?) of parameter (.*?) of student is invalid"));
    }

    @Test
    void createStudentWithIllegalFacultyNegativeTest() throws Exception {

        // Изначально факультет и так не задан

        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(STUDENT_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IllegalStudentFieldsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Value (.*?) of parameter (.*?) of student is invalid"));

        STUDENT_1.setFaculty(FACULTY_1);
        when(facultyRepositoryMock.findById(FACULTY_1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(STUDENT_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyNotFoundException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Faculty with (.*?) = (.*?) isn't found"));
    }

    @Test
    void getStudentPositiveTest() throws Exception {
        Student targetStudent = STUDENT_1;
        targetStudent.setFaculty(FACULTY_1);
        when(studentRepositoryMock.findById(targetStudent.getId())).thenReturn(Optional.of(targetStudent));

        mockMvc.perform(get("/students/{id}", targetStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(targetStudent.getId()))
                .andExpect(jsonPath("$.name").value(targetStudent.getName()))
                .andExpect(jsonPath("$.age").value(targetStudent.getAge()))

                .andExpect(jsonPath("$.faculty.id").value(targetStudent.getFaculty().getId()))
                .andExpect(jsonPath("$.faculty.name").value(targetStudent.getFaculty().getName()));
    }

    @Test
    void getStudentNegativeTest() throws Exception {

        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/{id}", STUDENT_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void getStudentByAgeExactMathTest() throws Exception {
        int targetAge = STUDENT_1.getAge();
        List<Student> expected = TEST_lIST_OF_4_STUDENTS.stream()
                .filter(student -> student.getAge() == targetAge)
                .toList();

        when(studentRepositoryMock.findByAge(STUDENT_1.getAge())).thenReturn(expected);

        String response = mockMvc.perform(get("/students?age={age}", targetAge)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Student> actual = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getStudentByAgeBetweenTest() throws Exception {
        int age = STUDENT_1.getAge();
        int upTo = STUDENT_1.getAge() + 10;
        List<Student> expected = TEST_lIST_OF_4_STUDENTS.stream()
                .filter(student -> student.getAge() >= age && student.getAge() <= upTo)
                .toList();

        when(studentRepositoryMock.findByAgeBetween(age, upTo)).thenReturn(expected);

        String response = mockMvc.perform(get("/students?age={age}&upTo={upTo}", age, upTo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Student> actual = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getStudentByWithoutMatchesTest() throws Exception {

        when(studentRepositoryMock.findByAge(STUDENT_1.getAge())).thenReturn(new ArrayList<>());

        String response = mockMvc.perform(get("/students?age={age}", STUDENT_1.getAge())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Student> actual = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(actual).isEqualTo(new ArrayList<>());
    }

    @Test
    void getFacultyOfStudentPositiveTest() throws Exception {
        STUDENT_1.setFaculty(FACULTY_1);
        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.of(STUDENT_1));

        mockMvc.perform(get("/students/{id}/faculty", STUDENT_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_1.getName()))
                .andExpect(jsonPath("$.color").value(FACULTY_1.getColor().toString()));
    }

    @Test
    void getFacultyOfStudentNegativeTest() throws Exception {
        STUDENT_1.setFaculty(FACULTY_1);
        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/{id}/faculty", STUDENT_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void updateStudentWithoutChangeFacultyPositiveTest() throws Exception {   // Без смены факультета
        Student srcStudent = STUDENT_4;
        srcStudent.setFaculty(FACULTY_1);
        Student destStudent = STUDENT_4_EDITED;
        destStudent.setFaculty(FACULTY_1);

        when(studentRepositoryMock.findById(srcStudent.getId())).thenReturn(Optional.of(srcStudent));
        when(facultyRepositoryMock.findById(srcStudent.getFaculty().getId())).thenReturn(Optional.of(srcStudent.getFaculty()));
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(destStudent);

        mockMvc.perform(put("/students/{id}", srcStudent.getId())
                        .content(objectMapper.writeValueAsString(destStudent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(destStudent.getId()))
                .andExpect(jsonPath("$.name").value(destStudent.getName()))
                .andExpect(jsonPath("$.age").value(destStudent.getAge()))
                .andExpect(jsonPath("$.faculty").value(destStudent.getFaculty()));
    }

    @Test
    void updateStudentWithChangeFacultyPositiveTest() throws Exception {   // Со сменой факультета

        /* Мы хотим изменить 4ого студента: сменить ему имя и перевести на второй факультет
           Саму логику пересборки факультетов с новыми студентами (метод FacultyService.updateFaculty()) замокаем,
           так как этот метод тестировался в своем классе */

//         В первом факультете студенты: 1, и 4
        FACULTY_1.setStudents(new ArrayList<>(List.of(STUDENT_1, STUDENT_4)));
        Student srcStudent = STUDENT_4;
        srcStudent.setFaculty(FACULTY_1);

//         Во втором факультете студенты: 2 и 3
        FACULTY_2.setStudents(new ArrayList<>(List.of(STUDENT_2, STUDENT_3)));
        Student destStudent = STUDENT_4_EDITED;
        destStudent.setFaculty(FACULTY_2);

        when(studentRepositoryMock.findById(destStudent.getId())).thenReturn(Optional.of(srcStudent));
        when(facultyRepositoryMock.findById(srcStudent.getFaculty().getId())).thenReturn(Optional.of(srcStudent.getFaculty()));
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(STUDENT_4_EDITED);

        // Зачислили студента во 2ой факультет
        FACULTY_2.setStudents(List.of(STUDENT_2, STUDENT_3, STUDENT_4_EDITED));
        doReturn(Optional.of(FACULTY_2)).when(facultyServiceImplSpy).updateFaculty(destStudent.getFaculty().getId(), FACULTY_2);

//         Отчисление студента не проверяем, тк эту информацию из ответа не получить. Она просто сохраняется на сервере

        mockMvc.perform(put("/students/{id}", srcStudent.getId())
                        .content(objectMapper.writeValueAsString(destStudent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(destStudent.getId()))
                .andExpect(jsonPath("$.name").value(destStudent.getName()))
                .andExpect(jsonPath("$.age").value(destStudent.getAge()))
                .andExpect(jsonPath("$.faculty").value(destStudent.getFaculty()));
    }

    @Test
    void updateNonexistentStudentNegativeTest() throws Exception {

        STUDENT_4_EDITED.setFaculty(FACULTY_1);

        mockMvc.perform(put("/students/{id}", -1L)   // Проверка по отрицательному id, но существующему факультету
                        .content(objectMapper.writeValueAsString(STUDENT_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        when(studentRepositoryMock.findById(FACULTY_4.getId())).thenReturn(Optional.empty());

        mockMvc.perform(put("/students/{id}", STUDENT_4.getId())   // Проверка по несуществующему факультету
                        .content(objectMapper.writeValueAsString(STUDENT_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void updateStudentWithNonexistentFacultyNegativeTest() throws Exception {

        when(studentRepositoryMock.findById(STUDENT_4.getId())).thenReturn(Optional.of(STUDENT_4));
        when(facultyRepositoryMock.findById(-1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/students/{id}", STUDENT_4.getId())   // Проверка, если Faculty == null
                        .content(objectMapper.writeValueAsString(STUDENT_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Value (.*?) of parameter (.*?) of student is invalid"));

        STUDENT_4_EDITED.setFaculty(FACULTY_1);

        mockMvc.perform(put("/students/{id}", STUDENT_4.getId())   // Проверка, если факультета нет в бд
                        .content(objectMapper.writeValueAsString(STUDENT_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Faculty with (.*?) = (.*?) isn't found"));
    }

    @Test
    void setAvatarPositiveTest() throws Exception {
// TODO: 08.03.2024 Перенести сет факультета в BeforeEach
        STUDENT_1.setFaculty(FACULTY_1);
        Student targetStudent = STUDENT_1;

        when(studentRepositoryMock.findById(targetStudent.getId())).thenReturn(Optional.of(targetStudent));
        when(avatarRepositoryMock.findByStudent_Id(targetStudent.getId())).thenReturn(Optional.empty());
        when(avatarRepositoryMock.save(any(Avatar.class))).thenReturn(AVATAR_1);
        when(studentRepositoryMock.save(any(Student.class))).thenReturn(targetStudent);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "avatar",
                testResoursePath().getFileName().toString(),
                Files.probeContentType(testResoursePath()),
                sentResource());

        mockMvc.perform(multipart(HttpMethod.PATCH, "/students/{id}/avatar", targetStudent.getId())
                        .file(multipartFile))

                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        Path expected = Path.of(finalSavedResource().formatted(targetStudent));
        assertThat(Files.exists(expected))
                .isTrue();
    }

    @Test
    void setAvatarNegativeTest() throws Exception {
        when(studentRepositoryMock.findById(FACULTY_1.getId())).thenReturn(Optional.empty());

        MockMultipartFile multipartFile = new MockMultipartFile(
                "avatar",
                testResoursePath().getFileName().toString(),
                Files.probeContentType(testResoursePath()),
                sentResource());

        mockMvc.perform(multipart(HttpMethod.PATCH, "/students/{id}/avatar", FACULTY_1.getId())
                        .file(multipartFile))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Student with (.*?) = (.*?) isn't found"));
    }

    @Test
    void getAvatarFromDbPositiveTest() {

    }

    @Test
    void deleteStudentPositiveTest() throws Exception {

        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.of(STUDENT_1));
        doNothing().when(studentRepositoryMock).delete(STUDENT_1);

        mockMvc.perform(delete("/students/{id}", STUDENT_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STUDENT_1.getId()))
                .andExpect(jsonPath("$.name").value(STUDENT_1.getName()))
                .andExpect(jsonPath("$.age").value(STUDENT_1.getAge()))
                .andExpect(jsonPath("$.faculty").value(STUDENT_1.getFaculty()));
    }

    @Test
    void deleteStudentNegativeTest() throws Exception {

        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/students/{id}", STUDENT_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
