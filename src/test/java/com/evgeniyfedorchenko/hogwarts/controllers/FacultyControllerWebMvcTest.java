package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyAlreadyExistsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import com.evgeniyfedorchenko.hogwarts.services.FacultyServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private FacultyRepository facultyRepositoryMock;
    @MockBean
    private StudentRepository studentRepositoryMock;
    @SpyBean
    private FacultyServiceImpl facultyServiceImplSpy;
    @InjectMocks
    private FacultyController facultyController;

    @BeforeEach
    public void beforeEach() {

        TEST_lIST_OF_4_FACULTY.forEach(faculty -> faculty.setStudents(new ArrayList<>()));

        testConstantsInitialisation();
        objectMapper = new ObjectMapper();
    }

    private void customizeObjectMapper() {
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            protected boolean _isIgnorable(Annotated a) {
                return super._isIgnorable(a) || a.getRawType() == Faculty.class;
            }
        });
    }

    @Test
    void createFacultyPositiveTest() throws Exception {
        when(facultyRepositoryMock.existsByName(FACULTY_1.getName())).thenReturn(false);
        when(facultyRepositoryMock.save(any(Faculty.class))).thenReturn(FACULTY_1);

        mockMvc.perform(post("/faculties")
                        .content(objectMapper.writeValueAsString(FACULTY_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_1.getName()))
                .andExpect(jsonPath("$.color").value(FACULTY_1.getColor().toString()));
    }

    @Test
    void createExistingFacultyNegativeTest() throws Exception {
        when(facultyRepositoryMock.existsByName(FACULTY_1.getName())).thenReturn(true);
        mockMvc.perform(post("/faculties")
                        .content(objectMapper.writeValueAsString(FACULTY_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyAlreadyExistsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .isEqualTo("Such a faculty already exists"));
    }

    @Test
    void createInvalidFacultyNegativeTest() throws Exception {
        FACULTY_1.setName(null);
        mockMvc.perform(post("/faculties")
                        .content(objectMapper.writeValueAsString(FACULTY_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IllegalFacultyFieldsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Value (.*?) of parameter (.*?) of faculty is invalid"));

        FACULTY_2.setColor(null);
        mockMvc.perform(post("/faculties")
                        .content(objectMapper.writeValueAsString(FACULTY_2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IllegalFacultyFieldsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .matches("Value (.*?) of parameter (.*?) of faculty is invalid"));
    }

    @Test
    void getFacultyPositiveTest() throws Exception {
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.of(FACULTY_1));

        mockMvc.perform(get("/faculties/{id}", FACULTY_1.getId())
                        .content(objectMapper.writeValueAsString(FACULTY_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_1.getName()))
                .andExpect(jsonPath("$.color").value(FACULTY_1.getColor().toString()));

    }

    @Test
    void getNonexistentFacultyNegativeTest() throws Exception {
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/faculties/{id}", FACULTY_1.getId())
                        .content(objectMapper.writeValueAsString(FACULTY_1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void getFacultyByColorAndPartNamePositiveTest() throws Exception {
        Color color = FACULTY_1.getColor();
        String namePart = FACULTY_1.getName().substring(1).toUpperCase();
        when(facultyRepositoryMock.findFacultyByColorAndNameContainsIgnoreCase(color, namePart))
                .thenReturn(List.of(FACULTY_1, FACULTY_2));

        String response = mockMvc.perform(get("/faculties?color={color}&namePart={namePart}", color, namePart)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Faculty> actual = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(actual).containsOnly(FACULTY_1, FACULTY_2);
    }

    @Test
    void getFacultyOnlyByNamePartPositiveTest() throws Exception {
        String namePart = FACULTY_1.getName().substring(1).toUpperCase();
        when(facultyRepositoryMock.findByNameContainsIgnoreCase(namePart))
                .thenReturn(List.of(FACULTY_1, FACULTY_2));

        String response = mockMvc.perform(get("/faculties?namePart={namePart}", namePart)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Faculty> actual = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(actual).containsOnly(FACULTY_1, FACULTY_2);
    }

    @Test
    void getFacultyByColorOrPartNamePartDoesNotThrownAnyExceptionsTest() throws Exception {

        when(facultyRepositoryMock.findByNameContainsIgnoreCase(anyString()))
                .thenReturn(new ArrayList<>());
        when(facultyRepositoryMock.findFacultyByColorAndNameContainsIgnoreCase(any(Color.class), anyString()))
                .thenReturn(new ArrayList<>());

        String response = mockMvc.perform(get("/faculties?namePart={namePart}", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Faculty> actual = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(actual)
                .doesNotContainNull()
                .isEmpty();
    }

    @Test
    void getStudentsOfFacultyPositiveTest() throws Exception {
        FACULTY_1.setStudents(List.of(STUDENT_1, STUDENT_2));
        when(studentRepositoryMock.findByFaculty_Id(FACULTY_1.getId()))
                .thenReturn(FACULTY_1.getStudents());


        String response = mockMvc.perform(get("/faculties/{id}/students", FACULTY_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        customizeObjectMapper();
        List<Student> actual = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(actual).isEqualTo(FACULTY_1.getStudents());
    }

    @Test
    void getStudentsOfFacultyNegativeTest() throws Exception {
        when(studentRepositoryMock.findByFaculty_Id(FACULTY_1.getId()))
                .thenReturn(new ArrayList<>());

        String response = mockMvc.perform(get("/faculties/{id}/students", FACULTY_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Student> actual = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(actual)
                .doesNotContainNull()
                .isEmpty();
    }

    @Test
    void updateFacultyPositiveTest() throws Exception {

        when(facultyRepositoryMock.findById(FACULTY_4.getId())).thenReturn(Optional.of(FACULTY_4));
        when(facultyRepositoryMock.findFirstByName(FACULTY_4_EDITED.getName())).thenReturn(Optional.empty());
        when(facultyRepositoryMock.save(FACULTY_4)).thenReturn(FACULTY_4);

        mockMvc.perform(put("/faculties/{id}", FACULTY_4.getId())
                        .content(objectMapper.writeValueAsString(FACULTY_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_4_EDITED.getName()))
                .andExpect(jsonPath("$.color").value(String.valueOf(FACULTY_4_EDITED.getColor())));
    }

    @Test
    void updateNonexistentFacultyNegativeTest() throws Exception {
        when(facultyRepositoryMock.findById(FACULTY_4.getId())).thenReturn(Optional.empty());

        mockMvc.perform(put("/faculties/{id}", FACULTY_4.getId())
                        .content(objectMapper.writeValueAsString(FACULTY_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void updateFacultyWithAlreadyExistingNegativeTest() throws Exception {
        /* Это исключение возникает, когда:
              1. Переданный в теле факультет содержит имя, которе уже занято другим факультетом, сохраненным в базе
              2. Id пришедшего факультета и сохранённого не совпадают
           В этом тесте сохраненный факультет - FACULTY_3 */

        Random random = new Random();
        while (FACULTY_4.getId().equals(FACULTY_3.getId())) {
            FACULTY_3.setId(random.nextLong());
        }
        when(facultyRepositoryMock.findById(FACULTY_4.getId())).thenReturn(Optional.of(FACULTY_4));
        when(facultyRepositoryMock.findFirstByName(FACULTY_4_EDITED.getName())).thenReturn(Optional.of(FACULTY_3));

        mockMvc.perform(put("/faculties/{id}", FACULTY_4.getId())
                        .content(objectMapper.writeValueAsString(FACULTY_4_EDITED))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyAlreadyExistsException.class))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .isEqualTo("This name already exists"));
    }

    @Test
    void deleteFacultyPositiveTest() throws Exception {

        when(facultyRepositoryMock.findById(FACULTY_1.getId())).thenReturn(Optional.of(FACULTY_1));
        doNothing().when(facultyRepositoryMock).delete(FACULTY_1);

        mockMvc.perform(delete("/faculties/{id}", FACULTY_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(FACULTY_1.getName()))
                .andExpect(jsonPath("$.color").value(FACULTY_1.getColor().toString()));
    }

    @Test
    void deleteFacultyNegativeTest() throws Exception {

        when(facultyRepositoryMock.findById(FACULTY_1.getId())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/faculties/{id}", FACULTY_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }
}
