package com.evgeniyfedorchenko.hogwarts.controllers;

import com.evgeniyfedorchenko.hogwarts.TestUtils;
import com.evgeniyfedorchenko.hogwarts.dto.AvatarDto;
import com.evgeniyfedorchenko.hogwarts.dto.FacultyOutputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentInputDto;
import com.evgeniyfedorchenko.hogwarts.dto.StudentOutputDto;
import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.mappers.AvatarMapper;
import com.evgeniyfedorchenko.hogwarts.mappers.FacultyMapper;
import com.evgeniyfedorchenko.hogwarts.mappers.StudentMapper;
import com.evgeniyfedorchenko.hogwarts.repositories.AvatarRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
public class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    /*   Repositories   */
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private StudentRepository studentRepository;

    /*   Mappers   */
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FacultyMapper facultyMapper;
    @Autowired
    private AvatarMapper avatarMapper;
    @Autowired
    private StudentMapper studentMapper;

    /*   Others   */
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private TestUtils testUtils;
    private List<Faculty> savedFaculties;
    private final List<Student> savedStudents = new ArrayList<>();
    private final Random random = new Random();

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

    @BeforeEach
    public void beforeEach() {
        testConstantsInitialisation();
        savedFaculties = facultyRepository.saveAll(TEST_lIST_OF_4_FACULTY);

        /* Всем студентам присваиваем рандомный факультет (сохраненный в БД) и тоже сохраняем
           А так же факультетам раздаем этих студентов и обновляем их в БД */
        TEST_lIST_OF_4_STUDENTS.forEach(student -> {
            Faculty randomFaculty = savedFaculties.get(random.nextInt(0, savedFaculties.size()));
            student.setFaculty(randomFaculty);
            Student savedStudent = studentRepository.save(student);
            facultyRepository.save(randomFaculty.addStudent(savedStudent));
            savedStudents.add(savedStudent);
        });
//        Обновляем переменную
        savedFaculties = facultyRepository.findAll();

        int randomNum = random.nextInt(0, savedFaculties.size());
        UNSAVED_STUDENT.setFaculty(savedFaculties.get(randomNum));
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
        savedStudents.clear();
        savedFaculties.clear();
    }

    @AfterAll
    static void stopContainers() {
        postgresContainer.stop();
    }

    private String baseStudentUrl() {
        return "http://localhost:%d/students".formatted(port);
    }

    private RestTemplate patchedRestTemplate(TestRestTemplate testRestTemplate) {
        RestTemplate patchRestTemplate = this.testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return patchRestTemplate;
    }

    @Test
    void createStudentsPositiveTest() {

        ResponseEntity<StudentOutputDto> responseEntity = testRestTemplate.postForEntity(
                baseStudentUrl(),
                testUtils.toInputDto(UNSAVED_STUDENT),
                StudentOutputDto.class);

        // Проверка ответа от сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(studentMapper.toDto(UNSAVED_STUDENT));

        // Проверка, что в бд и правда сохранен нужный объект
        Optional<Student> studentFromDb = studentRepository.findById(responseEntity.getBody().getId());
        assertThat(studentFromDb).isPresent();
        assertThat(studentFromDb).get()
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(UNSAVED_STUDENT);   /* -> UNSAVED_STUDENT - это локальная переменная.
                При сохранении его в БД, его факультет в курсе о нем (что покажет следующий ассерт)
                Но локально поле faculty у UNSAVED_STUDENT не обновлено, и него новый студент не добавляется */

        // Проверка, что в БД в факультет и правда зачислен свежесохраненный студент
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
                testUtils.toInputDto(UNSAVED_STUDENT),
                String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Validation error in parameter ([1-9])");
        assertThat(countStudentsBeforeAdding).isEqualTo(studentRepository.findAll());

        // Невалидный только возраст
        UNSAVED_STUDENT.setName("randomName");
        UNSAVED_STUDENT.setAge(0);

        ResponseEntity<String> responseEntity1 = testRestTemplate.postForEntity(
                baseStudentUrl(),
                testUtils.toInputDto(UNSAVED_STUDENT),
                String.class);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity1.getBody())
                .isNotNull()
                .matches("Validation error in parameter ([1-9])");
        assertThat(countStudentsBeforeAdding).isEqualTo(studentRepository.findAll());

        // Невалидный факультет
        StudentInputDto specialInputDto = testUtils.toInputDto(UNSAVED_STUDENT);
        specialInputDto.setFacultyId(0L);

        ResponseEntity<String> responseEntity2 = testRestTemplate.postForEntity(
                baseStudentUrl(),
                specialInputDto,
                String.class);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity2.getBody())
                .isNotNull()
                .matches("Validation error in parameter ([1-9])");

        assertThat(countStudentsBeforeAdding).isEqualTo(studentRepository.findAll());
    }

    @Test
    void getStudentPositiveTest() {
        Student expected = savedStudents.get(0);

        ResponseEntity<StudentOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}",
                StudentOutputDto.class,
                expected.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(studentMapper.toDto(expected));
    }

    @Test
    void getStudentNegativeTest() {

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}",
                String.class,
                -1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull()
                .isEqualTo("Validation errors:\n1. Id must be greater than 0\n");

//        Проверка потенциально годного, но несуществующего id
        long nonexistentId;
        List<Long> actualIds = studentRepository.findAll().stream().map(Student::getId).toList();
        do {
            nonexistentId = random.nextLong(1, Long.MAX_VALUE);
        } while (actualIds.contains(nonexistentId));
// TODO: 24.03.2024 вынести в метод нахождение несуществующего id
        ResponseEntity<String> responseEntity2 = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}",
                String.class,
                nonexistentId);

        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity2.getBody()).isNull();

    }

    @Test
    void getStudentsByAgeExactMatchTest() {

        Student targetStudent = savedStudents.get(0);
        ResponseEntity<List<StudentOutputDto>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/byAge?age={age}&upTo={upTo}",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                targetStudent.getAge(), targetStudent.getAge());

        List<StudentOutputDto> expected = savedStudents.stream()
                .filter(student -> student.getAge() == targetStudent.getAge())
                .map(s -> studentMapper.toDto(s))
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasSameElementsAs((expected));
    }

    @Test
    void getStudentsByAgeBetweenTest() {

        int age = savedStudents.get(0).getAge();
        int upTo;
        do {
            upTo = random.nextInt(0, Integer.MAX_VALUE);
        } while (upTo == age);

        if (upTo < age) {
            int temp = age;
            age = upTo;
            upTo = temp;
        }
        int finalAge = age;
        int finalUpTo = upTo;

        Collections.shuffle(savedStudents);
        List<StudentOutputDto> expected = savedStudents.stream()
                .filter(student -> student.getAge() >= finalAge && student.getAge() <= finalUpTo)
                .map(s -> studentMapper.toDto(s))
                .toList();

        ResponseEntity<List<StudentOutputDto>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/byAge?age={age}&upTo={upTo}",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                age, upTo
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasSameElementsAs(expected);
    }

    @Test
    void getStudentsByAgeWithoutMatchTest() {

        int age;
        int upTo;
        List<Integer> ages = savedStudents.stream().map(Student::getAge).toList();
        do {
            age = random.nextInt(1, Integer.MAX_VALUE);
            upTo = random.nextInt(1, Integer.MAX_VALUE);
        } while (ages.contains(age) && ages.contains(upTo));

        ResponseEntity<List<StudentOutputDto>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/byAge?age={age}&upTo={upTo}",
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                age, upTo
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull()
                .hasSize(0);
    }

    @Test
    void getFacultyOfStudentPositiveTest() {
        Student targetStudent = savedStudents.get(0);

        ResponseEntity<FacultyOutputDto> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}/faculty",
                FacultyOutputDto.class,
                targetStudent.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo(facultyMapper.toDto(targetStudent.getFaculty()));
    }

    @Test
    void getFacultyOfNonexistentStudentNegativeTest() {
        List<Long> ids = studentRepository.findAll().stream().map(Student::getId).toList();
        Long nonexistentId;
        do {
            nonexistentId = random.nextLong(0, Long.MAX_VALUE);
        } while (ids.contains(nonexistentId));

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}/faculty",
                String.class,
                nonexistentId);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void getFacultyOfStudentWithNegativeIdTest() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/{id}/faculty",
                String.class,
                -1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull()
                .isEqualTo("Validation errors:\n1. Id must be greater than 0\n");
    }

    @Test
    void updateStudentWithoutChangeFacultyPositiveTest() {

        Student targetStudent = savedStudents.get(0);
        STUDENT_4_EDITED.setFaculty(targetStudent.getFaculty());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        ResponseEntity<StudentOutputDto> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(studentMapper.toDto(STUDENT_4_EDITED), headers),
                StudentOutputDto.class,
                targetStudent.getId()
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(studentMapper.toDto(STUDENT_4_EDITED));

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
        Long oldFacultyIdOfTarget = targetStudent.getFaculty().getId();

        do {     // Ставим студенту "STUDENT_4_EDITED" факультет, отличный от факультета у студента "targetStudent"
            STUDENT_4_EDITED.setFaculty(savedFaculties.get(random.nextInt(0, savedFaculties.size())));
        }
        while (STUDENT_4_EDITED.getFaculty().getId().equals(oldFacultyIdOfTarget));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        ResponseEntity<StudentOutputDto> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(studentMapper.toDto(STUDENT_4_EDITED), headers),
                StudentOutputDto.class,
                targetStudent.getId()
        );
//        Проверка ответа сервера
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(studentMapper.toDto(STUDENT_4_EDITED));

//        Проверка, что в БД студент действительно обновился
        Student actualTarget = studentRepository.findById(targetStudent.getId()).orElseThrow();
        assertThat(actualTarget)
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(STUDENT_4_EDITED);   /* -> STUDENT_4_EDITED - это локальная переменная.
                При сохранении его в БД, его факультет в курсе о нем (что покажет последний ассерт)
                Но локально поле faculty у STUDENT_4_EDITED не обновлено, и него новый студент не добавляется */

//        Если у студента произошла смена факультета, то в базе факультетов это должно быть отражено
//        Старый факультет больше не содержит старого студента
        Faculty oldFaculty = facultyRepository.findById(oldFacultyIdOfTarget).orElseThrow();
        assertThat(oldFaculty.getStudents()).doesNotContain(targetStudent);

//        Проверка, что новый факультет действительно содержит обновленного студента
        Faculty newFaculty = facultyRepository.findById(actualTarget.getFaculty().getId())
                .orElseThrow();
        List<Long> studentIdsOfNewFaculty = newFaculty.getStudents()
                .stream()
                .map(Student::getId)
                .toList();

        assertThat(studentIdsOfNewFaculty)
                .contains(responseEntity.getBody().getId())
                .contains(targetStudent.getId());
    }

    @Test
    void updateStudentWithNonexistentFacultyNegativeTest() {
        List<Long> ids = facultyRepository.findAll().stream()
                .map(Faculty::getId)
                .toList();
        STUDENT_4_EDITED.setFaculty(savedFaculties.get(0));
        StudentInputDto inputDto = testUtils.toInputDto(STUDENT_4_EDITED);
        do {
            inputDto.setFacultyId(random.nextLong(0, Long.MAX_VALUE));
        } while (ids.contains(inputDto.getFacultyId()));

        STUDENT_4_EDITED.setFaculty(savedFaculties.get(0));
        Student expected = savedStudents.get(0);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(inputDto),
                String.class,
                expected.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).matches("FacultyId ([1-9]\\d*) not found");
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
        List<Long> ids = studentRepository.findAll().stream().map(Student::getId).toList();
        Long nonexistentId;
        do {
            nonexistentId = random.nextLong(0, Long.MAX_VALUE);
        } while (ids.contains(nonexistentId));

        int repoSizeBeforeDeleting = studentRepository.findAll().size();
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(baseStudentUrl() + "/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class,
                nonexistentId);

        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isNull();
        assertThat(studentRepository.findAll().size())
                .isEqualTo(repoSizeBeforeDeleting);
    }

    @Test
    void setAvatarPositiveTest() throws IOException {
//         given
        Student targetStudent = savedStudents.get(0);
        AVATAR_1.setFilePath(AVATAR_1.getFilePath().formatted(targetStudent));
        RestTemplate patchedRestTemplate = patchedRestTemplate(testRestTemplate);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatar", new FileSystemResource(testResourcePath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

//        invoking
        ResponseEntity<Boolean> responseEntity = patchedRestTemplate.exchange(
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

//        Проверка, что в БД к студенту привязался аватар и наоборот
        Student studentFromDb = studentRepository.findById(targetStudent.getId()).orElseThrow();
        Avatar avatarFromDb = avatarRepository.findById(studentFromDb.getAvatar().getId()).orElseThrow();
        assertThat(avatarFromDb)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id", "student", "data")   // Data в БД уже сжатая
                .isEqualTo(AVATAR_1);

        assertThat(avatarFromDb.getStudent())
                .usingRecursiveComparison()
                .isEqualTo(studentFromDb);

//        Наличие в локальном хранилище - из БД берем только путь и смотрим есть по этому пути наш файл локально
        Path pathToLocalFile = Path.of(avatarFromDb.getFilePath());
        assertThatCode(() -> Files.readAllBytes(pathToLocalFile))
                .doesNotThrowAnyException();
        assertThat(Files.readAllBytes(pathToLocalFile))
                .isNotEmpty()
                .isEqualTo(AVATAR_1.getData());

/*        Так как нельзя сравнить сжатое изображение с исходным, то сравним косвенные признаки успешного сжатия:
              1. Константа сжатия сработала (100px)
              2. Соотношение сторон сжатого изображение равно (с погрешностью) соотношению исходника
*/
        BufferedImage imageFromResource = ImageIO.read(new File(testResourcePath().toString()));
        BufferedImage imageFromDb = ImageIO.read(new ByteArrayInputStream(avatarFromDb.getData()));
        double resourceRatio = (double) imageFromResource.getWidth() / imageFromResource.getHeight();
        double fromDbRatio = (double) imageFromDb.getWidth() / imageFromDb.getHeight();

        assertThat(imageFromDb.getWidth() == 100).isTrue();
        assertThat(Math.abs(resourceRatio - fromDbRatio) < 0.05).isTrue();   // Среднее: 0,18
    }

    @Test
    void setAvatarForNonexistentStudentNegativeTest() {
        Student targetStudent = UNSAVED_STUDENT;
        RestTemplate patchedRestTemplate = patchedRestTemplate(testRestTemplate);

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatar", new FileSystemResource(testResourcePath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<String> responseEntity = patchedRestTemplate.exchange(
                baseStudentUrl() + "/{id}/avatar",
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                String.class,
                targetStudent.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Student with ID ([1-9]\\d*) not found for set Avatar");

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
    void getAvatarFromLocalPositiveTest(String urlEnding) throws IOException {

//        given
        Student student = savedStudents.get(0);
        Files.createDirectories(testResourceDir);   // Без проверки Files.exists() тк AfterEach позаботился об удалении директорий/файлов
        Files.write(Path.of(sentResourcePath().formatted(student)), Files.readAllBytes(testResourcePath()));

        // Связываем студента и аватар внутри бд, тк getAvatar() ищет аватар исходя из студента и его поля Avatar
        testUtils.linkStudentAndAvatar(student, AVATAR_1);

//        invoking
        ResponseEntity<byte[]> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + urlEnding,
                HttpMethod.GET,
                RequestEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                student.getId()
        );

//        assertions
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Сжатие проверяется в другом методе, тк тут мы руками положили байты в БД, то и ожидаем иж же
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isEqualTo(sentResourceBytes())
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
        Student student = savedStudents.get(0);

//        given
        testUtils.linkStudentAndAvatar(student, AVATAR_1);

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

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .matches("Student with ID ([1-9]\\d*) not found for get Avatar");

        long sizeAfter = 0;
        try (Stream<Path> filesWalk = Files.walk(Paths.get(testResourceDir.toUri()))) {
            sizeAfter = filesWalk.mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(sizeBefore == sizeAfter).isTrue();
    }

    @Test
    void getNumberOfStudents() {
        ResponseEntity<Long> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/quantity",
                Long.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(studentRepository.count());
    }

    @Test
    void getAverageAge() {
        Double expected = (double) studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .sum() / studentRepository.count();

        ResponseEntity<Double> responseEntity = testRestTemplate.getForEntity(
                baseStudentUrl() + "/avg-age",
                Double.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expected);
    }

    @Test
    void getAllAvatars() {
        int pageNumber = random.nextInt(1, 3);
        int pageSize = random.nextInt(1, 6);


        ResponseEntity<List<AvatarDto>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "/avatars?pageNumber={pageNumber}&pageSize={pageSize}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                pageNumber, pageSize
        );

        List<AvatarDto> actual = avatarRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .get()
                .map(avatarMapper::toDto)
                .toList();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSameElementsAs(actual);
    }

    @Test
    void searchStudentsTest() {

        String sortParam = List.of("id", "name", "age").get(random.nextInt(0, 2));
        SortOrder sortOrder = random.nextBoolean() ? SortOrder.ASC : SortOrder.DESC;
        int pageNumber = 3;
        int pageSize = 1;

        List<StudentOutputDto> actual = studentRepository.findAll().stream()
                .sorted(Comparator.comparing(Student::getId))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .map(studentMapper::toDto)
                .toList();

        ResponseEntity<List<StudentOutputDto>> responseEntity = testRestTemplate.exchange(
                baseStudentUrl() + "?sortParam={sortParam}&sortOrder={sortOrder}&pageNumber={pageNumber}&pageSize={pageSize}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {},
                sortParam, sortOrder, pageNumber, pageSize
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSameElementsAs(actual);

    }
}