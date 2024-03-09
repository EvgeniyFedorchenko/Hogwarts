package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
        registry.add("spring.datasource.hikari.auto-commit", () -> true);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private StudentRepository studentRepository;

    private List<Faculty> savedFaculties;
    private List<Student> savedStudents;

    @Autowired
    private TestRestTemplate testRestTemplate;
    private RestTemplate patchRestTemplate;

    @BeforeEach
    public void beforeEach() {
        testConstantsInitialisation();
        savedFaculties = facultyRepository.saveAll(TEST_lIST_OF_4_FACULTY);

//        Всем студентам присваиваем рандомный факультет (сохраненный в БД) и тоже сохраняем
        Random rand = new Random();
        savedStudents = studentRepository.saveAll(TEST_lIST_OF_4_STUDENTS
                .stream()
                .peek(student -> {
                    int randomFaculty = rand.nextInt(0, savedFaculties.size());
                    student.setFaculty(savedFaculties.get(randomFaculty));
                })
                .toList());

        int randomNum = rand.nextInt(0, savedFaculties.size());
        UNSAVED_STUDENT.setFaculty(savedFaculties.get(randomNum));

//        Для метода PATCH localhost:port/students/{id}/avatar
        this.patchRestTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        this.patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @AfterEach
    public void afterEach() throws IOException {

        studentRepository.deleteAll();
        facultyRepository.deleteAll();
        avatarRepository.deleteAll();

        if (Files.exists(testResourceDir)) {
            File[] listFiles = new File(testResourceDir.toUri()).listFiles();
            if (listFiles != null) {
                Arrays.stream(listFiles).forEach(File::delete);
            }
        }
        Files.deleteIfExists(testResourceDir);
    }

    @AfterAll
    static void stopContainers() {
        postgresContainer.stop();
    }

    private String baseStudentUrl() {
        return "http://localhost:%d/students".formatted(port);
    }

    @Test
    void createStudentsPositiveTest() {

        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(
                baseStudentUrl(),
                UNSAVED_STUDENT,
                Student.class);

        // Проверка ответа от сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(UNSAVED_STUDENT);

        // Проверка, что в бд и правда сохранен нужный объект
        Optional<Student> studentFromDb = studentRepository.findById(responseEntity.getBody().getId());
        assertThat(studentFromDb).isPresent();
        assertThat(studentFromDb).get()
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(UNSAVED_STUDENT);

        // Проверка, что в факультет и правда зачислен свежесохраненный студент
        Optional<Faculty> facultyFromDb = facultyRepository.findById(UNSAVED_STUDENT.getFaculty().getId());
        assertThat(facultyFromDb).isPresent();
        assertThat(facultyFromDb.get().getStudents())
                .contains(studentFromDb.get());
    }

    @Test
    void createStudentWithIllegalFieldsNegativeTest() {

        // Невалидное только имя
        UNSAVED_STUDENT.setName(null);
        List<Student> countStudentsBeforeAdding = studentRepository.findAll();

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                baseStudentUrl(),
                UNSAVED_STUDENT,
                String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Value (.+?) of parameter (.+?) of student is invalid");
        assertThat(countStudentsBeforeAdding).isEqualTo(studentRepository.findAll());

        // Невалидный только возраст
        UNSAVED_STUDENT.setName("randomName");
        UNSAVED_STUDENT.setAge(0);

        ResponseEntity<String> responseEntity1 = testRestTemplate.postForEntity(
                baseStudentUrl(),
                UNSAVED_STUDENT,
                String.class);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity1.getBody())
                .isNotNull()
                .matches("Value (.+?) of parameter (.+?) of student is invalid");
        assertThat(countStudentsBeforeAdding).isEqualTo(studentRepository.findAll());

        // Студент с faculty = null
        ResponseEntity<String> responseEntity2 = testRestTemplate.postForEntity(
                baseStudentUrl(),
                STUDENT_WITHOUT_FACULTY,
                String.class);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity2.getBody())
                .isNotNull()
                .matches("Value (.+?) of parameter (.+?) of student is invalid");
        assertThat(countStudentsBeforeAdding).isEqualTo(studentRepository.findAll());
    }

    @Test
    void getStudentPositiveTest() {
        Student expected = savedStudents.get(0);

        ResponseEntity<Student> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}",
                Student.class,
                expected.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getStudentNegativeTest() {

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}",
                String.class,
                -1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void getStudentsByAgeExactMatchTest() {

        Student targetStudent = savedStudents.get(0);
        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "?age=" + targetStudent.getAge(),
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        List<Student> expected = savedStudents.stream()
                .filter(student -> student.getAge() == targetStudent.getAge())
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo(expected);

    }

    @Test
    void getStudentsByAgeBetweenTest() {

        List<Student> expected = savedStudents.stream()
                .sorted(Comparator.comparingInt(Student::getAge))
                .toList()
                .subList(0, 3);

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "?age=%s&upTo=%s".formatted(expected.get(0).getAge(), expected.get(expected.size() - 1).getAge()),
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .containsAll(expected)
                .containsOnlyOnceElementsOf(expected);
    }

    @Test
    void getStudentsByAgeWithoutMatchTest() {

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "?age=-1",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void getFacultyOfStudentPositiveTest() {
        Student targetStudent = savedStudents.get(0);

        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}/faculty",
                Faculty.class,
                targetStudent.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("students")
                .isEqualTo(targetStudent.getFaculty());
    }

    @Test
    void getFacultyOfStudentNegativeTest() {
        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}/faculty",
                Faculty.class,
                -1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void updateStudentWithoutChangeFacultyPositiveTest() {

        Student targetStudent = savedStudents.get(0);
        STUDENT_4_EDITED.setFaculty(targetStudent.getFaculty());

        ResponseEntity<Student> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(STUDENT_4_EDITED),
                Student.class,
                targetStudent.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(STUDENT_4_EDITED);

        Optional<Student> fromDb = studentRepository.findById(targetStudent.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(STUDENT_4_EDITED);
    }

    @Test
    void updateStudentWithChangeFacultyPositiveTest() {
        Student targetStudent = savedStudents.get(0);
        Faculty oldFacultyOfTarget = targetStudent.getFaculty();
        // Ставим студенту "STUDENT_4_EDITED" факультет, отличный от факультета у студента "expected"
        for (Faculty faculty : savedFaculties) {
            if (!faculty.equals(targetStudent.getFaculty())) {
                STUDENT_4_EDITED.setFaculty(faculty);
            }
        }
        testRestTemplate.put(
                baseStudentUrl() + "/{id}",
                STUDENT_4_EDITED,
                targetStudent.getId());

        Optional<Student> actual = studentRepository.findById(targetStudent.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(STUDENT_4_EDITED);

        // Если у студента произошла смена факультета, то в базе факультетов это должно быть отражено
        assertThat(oldFacultyOfTarget.getStudents()).doesNotContain(targetStudent);

        Optional<Faculty> oldFacultyOfStudent = facultyRepository.findById(actual.get().getFaculty().getId());
        assertThat(oldFacultyOfStudent).isPresent();
        assertThat(oldFacultyOfStudent.get().getStudents()).contains(targetStudent);
    }


    @Test
    void updateStudentWithIllegalFieldsNegativeTest() {
        Student expected = savedStudents.get(0);
        expected.setName(null);

        testRestTemplate.put(
                baseStudentUrl() + "/{id}",
                STUDENT_4_EDITED,
                expected.getId());

        Optional<Student> fromDb = studentRepository.findById(expected.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getName()).isNotNull();

        expected.setName("randomName");
        expected.setAge(0);

        testRestTemplate.put(
                baseStudentUrl() + "/{id}",
                STUDENT_4_EDITED,
                expected.getId());

        Optional<Student> fromDb1 = studentRepository.findById(expected.getId());
        assertThat(fromDb1).isPresent();
        assertThat(fromDb1.get().getAge()).isNotEqualTo(0);
    }

    @Test
    void updateStudentWithoutFacultyNegativeTest() {
        Student expected = savedStudents.get(0);

        testRestTemplate.put(
                baseStudentUrl() + "/{id}",
                STUDENT_WITHOUT_FACULTY,
                expected.getId());

        Optional<Student> fromDb = studentRepository.findById(expected.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("faculty.students")
                .isEqualTo(expected);
    }

    @Test
    void deleteStudentPositiveTest() {

        Student expected = savedStudents.get(0);
        int repoSizeBeforeDeleting = studentRepository.findAll().size();

        testRestTemplate.delete(baseStudentUrl() + "/{id}", expected.getId());

        Optional<Student> fromDb = studentRepository.findById(expected.getId());
        assertThat(fromDb).isEmpty();
        assertThat(studentRepository.findAll().size())
                .isNotEqualTo(repoSizeBeforeDeleting);
    }

    @Test
    void deleteStudentNegativeTest() {
        int repoSizeBeforeDeleting = studentRepository.findAll().size();
        testRestTemplate.delete(baseStudentUrl() + "/{id}", -1L);

        assertThat(studentRepository.findAll().size()).isEqualTo(repoSizeBeforeDeleting);
    }

    @Test
    void setAvatarPositiveTest() throws IOException {
//         given
        Student targetStudent = savedStudents.get(0);
        AVATAR_1.setFilePath(AVATAR_1.getFilePath().formatted(targetStudent));

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatar", new FileSystemResource(testResoursePath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

//        invoking
        ResponseEntity<Boolean> responseEntity = patchRestTemplate.exchange(
                baseStudentUrl() + "/{id}/avatar",
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                Boolean.class,
                targetStudent.getId());

//        assertions
//        проверка ответа
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isTrue();

//        Проверка заполненного поля Avatar avatar у студента в БД
        Optional<Student> studentFromDb = studentRepository.findById(targetStudent.getId());

        assertThat(studentFromDb).isPresent();
        assertThat(studentFromDb.get().getAvatar())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "student")
                .isEqualTo(AVATAR_1);

//         Наличие в БД
        Optional<Avatar> fromAvatarRepo = avatarRepository.findById(studentFromDb.get().getAvatar().getId());

        assertThat(fromAvatarRepo).isPresent();
        assertThat(fromAvatarRepo.get().getData()).isEqualTo(sentResourceBytes());

//        Наличие в локальном хранилище - из БД берем только путь и смотрим есть по этому пути наш файл локально
        Path pathToLocalFile = Path.of(fromAvatarRepo.get().getFilePath());

        assertThatCode(() -> Files.readAllBytes(pathToLocalFile))
                .doesNotThrowAnyException();
        assertThat(Files.readAllBytes(pathToLocalFile))
                .isNotEmpty()
                .isEqualTo(AVATAR_1.getData());
    }

    @Test
    void setAvatarIfStudentNotFoundNegativeTest() {
        Student targetStudent = UNSAVED_STUDENT;
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatar", new FileSystemResource(testResoursePath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<String> responseEntity = patchRestTemplate.exchange(
                baseStudentUrl() + "/{id}/avatar",
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                String.class,
                targetStudent.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Student with (.+?) = (.+?) isn't found");

        assertThat(Files.exists(testResourceDir))
                .isFalse();
        assertThat(Files.exists(Path.of(sentResourcePath().formatted(targetStudent))))
                .isFalse();
    }

    @MethodSource
    public static Stream<Arguments> provideParamsForGetAvatarTests() {
        return Stream.of(
                Arguments.of("/{id}/avatar?large=true"),   // Проверка метода получения изображения с диска
                Arguments.of("/{id}/avatar?large=false")   // Проверка метода получения изображения из БД
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForGetAvatarTests")
    void getAvatarPositiveTest(String url) throws IOException {

//        given
        Student student = savedStudents.get(0);
        Files.createDirectories(testResourceDir);   // Без проверки Files.exists() тк AfterEach позаботился об удалении директорий/файлов
        Files.write(Path.of(sentResourcePath().formatted(student)), Files.readAllBytes(testResoursePath()));

        // Связываем студента и аватар внутри бд, тк getAvatar() ищет аватар исходя из студента и его поля Avatar
        AVATAR_1.setId(null);
        AVATAR_1.setFilePath(AVATAR_1.getFilePath().formatted(student));
        AVATAR_1.setStudent(student);
        Avatar savedAvatar = avatarRepository.save(AVATAR_1);

        student.setAvatar(savedAvatar);
        Student expectedStudent = studentRepository.save(student);

//        invoking
        ResponseEntity<byte[]> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + url,
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                expectedStudent.getId()
        );

//        assertions
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo(AVATAR_1.getData());

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.parseMediaType(AVATAR_1.getMediaType()));
        expectedHeaders.setContentLength(AVATAR_1.getData().length);

        assertThat(responseEntity.getHeaders())
                .isNotNull()
                .containsAllEntriesOf(expectedHeaders);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForGetAvatarTests")
    void getAvatarIfStudentNotFoundNegativeTest(String url) throws IOException {
//        given
        Student student = savedStudents.get(0);
        Files.createDirectories(testResourceDir);
        Files.write(Path.of(sentResourcePath().formatted(student)), Files.readAllBytes(testResoursePath()));

        AVATAR_1.setId(null);
        AVATAR_1.setFilePath(AVATAR_1.getFilePath().formatted(student));
        AVATAR_1.setStudent(student);
        Avatar savedAvatar = avatarRepository.save(AVATAR_1);
        student.setAvatar(savedAvatar);
        studentRepository.save(student);

        long sizeBefore = 0;
        try (Stream<Path> filesWalk = Files.walk(Paths.get(testResourceDir.toUri()))) {
            sizeBefore = filesWalk
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + url,
                HttpMethod.GET,
                RequestEntity.EMPTY,
                String.class,
                UNSAVED_STUDENT.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Student with (.+?) = (.+?) isn't found");

        long sizeAfter = 0;
        try (Stream<Path> filesWalk = Files.walk(Paths.get(testResourceDir.toUri()))) {
            sizeAfter = filesWalk.mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(sizeBefore == sizeAfter).isTrue();
    }
}