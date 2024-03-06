package com.evgeniyfedorchenko.hogwarts.controllers;


import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
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

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTestTest {

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

    @BeforeEach
    public void beforeEach() {
        testConstantsInitialisation();
        facultyRepository.save(FACULTY_1);
    }

    @AfterEach
    public void afterEach() {
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
                FACULTY_2,
                Faculty.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty actualBody = responseEntity.getBody();
        assertThat(actualBody).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "students")
                .isEqualTo(FACULTY_2);
        assertThat(actualBody.getId()).isNotNull();

        Optional<Faculty> actual = facultyRepository.findById(actualBody.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("students")
                .isEqualTo(actualBody);
    }

    @RepeatedTest(10)
    void createFacultyWithoutAnyFieldsNegativeTest() {
        Random random = new Random();
        if (random.nextBoolean()) {
            FACULTY_2.setName(null);
        } else {
            FACULTY_2.setColor(null);
        }
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                baseFacultyUrl(),
                FACULTY_2,
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
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "students")
                .isEqualTo(List.of(FACULTY_1));
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
        Faculty faculty = facultyRepository.findFirstByName(FACULTY_1.getName()).orElseThrow();
        STUDENT_1.setFaculty(faculty);
        STUDENT_2.setFaculty(faculty);
        studentRepository.saveAll(List.of(STUDENT_1, STUDENT_2));

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                faculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(STUDENT_1, STUDENT_2));
    }

    @Test
    void deleteFacultyPositiveTest() {
        Long facultyId = facultyRepository.findFirstByName(FACULTY_1.getName()).map(Faculty::getId).orElseThrow();

        ResponseEntity<Faculty> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.DELETE,
                null,
                Faculty.class,
                facultyId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id", "student")
                .isEqualTo(FACULTY_1);
        assertThat(facultyRepository.findById(facultyId)).isEmpty();
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
        Faculty faculty = facultyRepository.save(FACULTY_4);

        ResponseEntity<Faculty> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(FACULTY_4_EDITED),
                Faculty.class,
                faculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "student")
                .isEqualTo(FACULTY_4_EDITED);

        assertThat(facultyRepository.findById(faculty.getId())).isPresent();
        assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id", "student")
                .isNotEqualTo(FACULTY_4);

    }

    @Test
    void updateFacultyWithAnyNullFieldsNegativeTest() {
        Faculty faculty = facultyRepository.save(FACULTY_4);
        FACULTY_4_EDITED.setName(null);
        FACULTY_4_EDITED.setColor(null);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(FACULTY_4_EDITED),
                String.class,
                faculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Value (.+?) of parameter (.+?) of faculty is invalid");

        assertThat(facultyRepository.findById(faculty.getId())).isPresent();
    }

    @Test
    void updateNonexistentFacultyNegativeTest() {
        Faculty faculty = facultyRepository.save(FACULTY_4);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(FACULTY_4_EDITED),
                String.class,
                -1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();

    }

    @Test
    void updateFacultyToExistentNameNegativeTest() {
        Faculty faculty = facultyRepository.save(FACULTY_4);
        FACULTY_4_EDITED.setName(FACULTY_1.getName());

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseFacultyUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(FACULTY_4_EDITED),
                String.class,
                faculty.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo("This name already exists");
    }
}