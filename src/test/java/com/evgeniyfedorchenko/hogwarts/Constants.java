package com.evgeniyfedorchenko.hogwarts;

import com.evgeniyfedorchenko.hogwarts.entities.Avatar;
import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.AvatarProcessingException;
import net.datafaker.Faker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.getFilenameExtension;

public class Constants {

    public static final Faculty FACULTY_1 = new Faculty();
    public static final Faculty FACULTY_2 = new Faculty();
    public static final Faculty FACULTY_3 = new Faculty();
    public static final Faculty FACULTY_4 = new Faculty();
    public static final Faculty FACULTY_4_EDITED = new Faculty();


    /**
     * Директория, в которой сохраняется ресурс во время теста.
     * Удаляется после теста через @AfterEach
     */
//    @Value("${path.to.avatars.folder}")
//    private static Path dir;
    public static Path testAvatarsDir;

// TODO: 06.03.2024 поправить названия методов и описание
    /**
     * Путь, по которому должна находиться ресурс после окончания теста.
     * Необходимо ".formatted(Student targetStudent)" для получения пути.
     * Директория удаляется после теста через @AfterEach)
     */
    public static String finalSavedResource() {
        return testAvatarsDir + "\\%s."+ getFilenameExtension(String.valueOf(testResoursePath()));
    }

    /**
     * Данные изображения, прочитанные из тестовых исходных ресурсов.
     * Предназначены для отправки и последующего тестирования
     */
    public static byte[] sentResource() {
        try {
            return Files.readAllBytes(testResoursePath());   // Исходник, который отправляем
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Путь к ресурсу, находящемуся в директории тестовых ресурсов
     */
    public static Path testResoursePath() {
        return Path.of("src/test/resources/static/image.jpg");
    }

    public static final Avatar AVATAR_1 = new Avatar();


    public static final Student STUDENT_1 = new Student();
    public static final Student STUDENT_2 = new Student();
    public static final Student STUDENT_3 = new Student();
    public static final Student STUDENT_4 = new Student();
    public static final Student STUDENT_4_EDITED = new Student();
    public static final Student UNSAVED_STUDENT = new Student();
    public static final Student STUDENT_WITHOUT_FACULTY = new Student();

    public static final List<Student> TEST_lIST_OF_4_STUDENTS = new ArrayList<>(List.of(
            STUDENT_1, STUDENT_2, STUDENT_3, STUDENT_4));
    public static final List<Faculty> TEST_lIST_OF_4_FACULTY = new ArrayList<>(List.of(
            FACULTY_1, FACULTY_2, FACULTY_3, FACULTY_4));
    private static final Faker faker = new Faker();


    public static void testConstantsInitialisation() {

        testAvatarsDir = Path.of("src/test/resources/avatars");
        facultiesConstantsInitialize();
        avatarConstantsInitialize();
        studentsConstantsInitialize();


    }

    private static void facultiesConstantsInitialize() {
        Stream.of(FACULTY_1, FACULTY_2, FACULTY_3, FACULTY_4)
                .forEach(faculty -> {
                    faculty.setId(faker.random().nextLong(100, 100));
                    faculty.setName(faker.letterify("????????"));
                    faculty.setColor(Color.values()[faker.random().nextInt(Color.values().length)]);
                    faculty.setStudents(new ArrayList<>());
                });

        FACULTY_4_EDITED.setId(FACULTY_4.getId());
        FACULTY_4_EDITED.setName(FACULTY_4.getName() + "Edited");
        FACULTY_4_EDITED.setColor(FACULTY_4.getColor());
        FACULTY_4_EDITED.setStudents(FACULTY_4.getStudents());
    }


    private static void avatarConstantsInitialize() {
        try {
            AVATAR_1.setMediaType(String.valueOf(Files.probeContentType(testResoursePath())));
            AVATAR_1.setFilePath(finalSavedResource());
            AVATAR_1.setData(sentResource());
        } catch (IOException e) {
            throw new AvatarProcessingException("Test-avatar initialising filed", e);
        }
    }


    private static void studentsConstantsInitialize() {

        Stream.of(STUDENT_1, STUDENT_2, STUDENT_3, STUDENT_4, STUDENT_4_EDITED,
                        STUDENT_WITHOUT_FACULTY, UNSAVED_STUDENT)
                .forEach(student -> {
                    student.setId(faker.random().nextLong(100, 100));
                    student.setName(faker.letterify("????????"));
                    student.setAge(faker.random().nextInt(18, 30));
                });

        STUDENT_4_EDITED.setName(STUDENT_4.getName() + "Edited");
        STUDENT_4_EDITED.setFaculty(STUDENT_4.getFaculty());

        STUDENT_WITHOUT_FACULTY.setName("StudentWithoutFaculty");
    }
}
