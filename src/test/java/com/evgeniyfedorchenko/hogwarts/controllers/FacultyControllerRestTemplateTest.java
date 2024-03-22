package com.evgeniyfedorchenko.hogwarts.controllers;


import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTemplateTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AvatarRepository avatarRepository;

    private List<Faculty> savedFaculties;


    @BeforeEach
    void beforeEach() {
        testConstantsInitialisation();
        TEST_lIST_OF_4_FACULTY.forEach(faculty -> faculty.setStudents(new ArrayList<>()));
        TEST_lIST_OF_4_STUDENTS.forEach(student -> student.setAvatar(null));

        savedFaculties = facultyRepository.saveAll(TEST_lIST_OF_4_FACULTY);
        Random rand = new Random();

        studentRepository.saveAll(TEST_lIST_OF_4_STUDENTS.stream()
                .peek(student -> {
                    Faculty randomFaculty = savedFaculties.get(rand.nextInt(0, savedFaculties.size()));
                    student.setFaculty(randomFaculty);
                }).toList());

        savedFaculties = facultyRepository.saveAll(savedFaculties.stream()
                .peek(faculty -> {
                    List<Student> stdns = studentRepository.findByFaculty_Id(faculty.getId());
                    faculty.setStudents(stdns);
                    facultyRepository.save(faculty);
                }).toList());
    }

    @AfterEach
    void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @AfterAll
    static void stopContainers() {
        postgresContainer.stop();
    }

    private String baseFacultyUrl() {
        return "http://localhost:%d/faculties".formatted(port);
    }

    @Test
    void createFacultyPositiveTest() {

        ResponseEntity<Faculty> responseEntity = testRestTemplate.postForEntity(
                baseFacultyUrl(),
                UNSAVED_EMPTY_FACULTY,
                Faculty.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty actualBody = responseEntity.getBody();
        assertThat(actualBody).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "students")
                .isEqualTo(UNSAVED_EMPTY_FACULTY);
        assertThat(actualBody.getId()).isNotNull();

        Optional<Faculty> actual = facultyRepository.findById(actualBody.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("students")
                .isEqualTo(actualBody);
    }

    @Test
    void createFacultyWithoutAnyFieldsNegativeTest() {
        Faculty invalidFaculty = savedFaculties.get(0);
        invalidFaculty.setName(null);
        invalidFaculty.setColor(null);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                baseFacultyUrl(),
                invalidFaculty,
                String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Value (.+?) of parameter (.+?) of faculty is invalid");
    }

    @Test
    void createExistingFacultyNegativeTest() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                baseFacultyUrl(),
                FACULTY_1,
                String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo("Such a faculty already exists");
    }

    @Test
    void getFacultyPositiveTest() {
        Optional<Faculty> expected = facultyRepository.findFirstByName(FACULTY_1.getName());
        Long facultyId = expected.map(Faculty::getId).orElseThrow();

        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                baseFacultyUrl() + "/{id}",
                Faculty.class,
                facultyId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(expected).isPresent();
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("students")
                .isEqualTo(expected.get());

    }

    @Test
    void getFacultyNegativeTest() {
        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                baseFacultyUrl() + "/{id}",
                Faculty.class,
                -1L);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void getFacultyByColorAndPartNamePositiveTest() {
        Color color = FACULTY_1.getColor();
        String namePart = FACULTY_1.getName().substring(1);

        ResponseEntity<List<Faculty>> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "?color=%s&namePart=%s".formatted(color, namePart),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "students")
                .isEqualTo(List.of(FACULTY_1));
    }

    @Test
    void getFacultyByOnlyColorPositiveTest() {
        Color color = FACULTY_1.getColor();

        ResponseEntity<List<Faculty>> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "?color=%s".formatted(color),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .comparingOnlyFields("name", "color", "students");
    }


    @Test
    void getFacultyByColorAndPartNameWithoutMatchPositiveTest() {
        String namePart = "SSS";

        ResponseEntity<List<Faculty>> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "?namePart=%s".formatted(namePart),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEmpty();
    }

    @Test
    void getStudentsOfFaculty() {
        Faculty targetFaculty = savedFaculties.get(0);

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                targetFaculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty")
                .isEqualTo(targetFaculty.getStudents());
    }

    @Test
    void deleteFacultyPositiveTest() {
        Optional<Faculty> firstByName = facultyRepository.findFirstByName(FACULTY_1.getName());
        if (firstByName.isEmpty()) {
            throw new RuntimeException("Faculty not found");
        }
        ResponseEntity<Faculty> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.DELETE,
                null,
                Faculty.class,
                firstByName.get().getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id", "students")
                .isEqualTo(firstByName.get());
        assertThat(facultyRepository.findById(firstByName.get().getId())).isEmpty();
    }

    @Test
    void deleteFacultyNegativeTest() {

        ResponseEntity<Faculty> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.DELETE,
                null,
                Faculty.class,
                -1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void updateFacultyPositiveTest() {
        Faculty oldFaculty = savedFaculties.get(0);

        ResponseEntity<Faculty> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(FACULTY_4_EDITED),
                Faculty.class,
                oldFaculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "students")
                .isEqualTo(FACULTY_4_EDITED);

        Optional<Faculty> byId = facultyRepository.findById(oldFaculty.getId());
        assertThat(byId).isPresent();
        assertThat(byId.get().getName()).isEqualTo(FACULTY_4_EDITED.getName());
        assertThat(byId.get().getColor()).isEqualTo(FACULTY_4_EDITED.getColor());
    }

    @Test
    void updateFacultyWithAnyNullFieldsNegativeTest() {
        Optional<Faculty> faculty = facultyRepository.findFirstByName(FACULTY_4.getName());
        if (faculty.isEmpty()) {
            throw new RuntimeException("Faculty not found");
        }
        faculty.get().setName(null);
        faculty.get().setColor(null);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(faculty),
                String.class,
                faculty.get().getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Value (.+?) of parameter (.+?) of faculty is invalid");

        assertThat(facultyRepository.findById(faculty.get().getId())).isPresent();
    }

    @Test
    void updateNonexistentFacultyNegativeTest() {


        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(FACULTY_4_EDITED),
                String.class,
                UNSAVED_EMPTY_FACULTY.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();

    }

    @Test
    void updateFacultyToExistentNameNegativeTest() {
        Faculty faculty = savedFaculties.get(0);
        faculty.setName(savedFaculties.get(1).getName());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(faculty),
                String.class,
                faculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo("This name already exists");
    }
}