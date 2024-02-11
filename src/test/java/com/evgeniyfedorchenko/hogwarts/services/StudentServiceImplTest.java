package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.evgeniyfedorchenko.hogwarts.services.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class StudentServiceImplTest {

    private final StudentServiceImpl out = new StudentServiceImpl();

    @BeforeEach
    public void beforeEach() {
        out.createStudent(STUDENT_1);
        out.createStudent(STUDENT_2);
    }

    @AfterEach
    public void afterEach() {
        Long id = 1L;
        while (out.getStudent(id).isPresent()) {
            out.deleteStudent(id);
        }
    }
    @Test
    void createStudentTest() {
        Student actual = out.createStudent(STUDENT_3);
        assertThat(actual).isEqualTo(STUDENT_3);
        assertThat(actual.getId()).isEqualTo(3);
        assertThat(out.getStudent(3L).get()).isEqualTo(STUDENT_3);
    }

    @Test
    void getStudentPositiveTest() {
        Student actual = out.getStudent(2L).get();
        assertThat(actual).isEqualTo(STUDENT_2);
    }

    @Test
    void getStudentNegativeTest() {
        Optional<Student> actual = out.getStudent(3L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateStudentPositiveTest() {
        Student actual = out.updateStudent(2L, STUDENT_4).get();
        assertThat(actual).isEqualTo(STUDENT_2);
    }

    @Test
    void updateStudentNegativeTest1() {
        Optional<Student> actual = out.updateStudent(-1L, STUDENT_4);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateStudentNegativeTest2() {
        Optional<Student> actual = out.updateStudent(2L, null);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteStudentTest() {
        Student actual = out.deleteStudent(2L).get();
        assertThat(actual).isEqualTo(STUDENT_2);
    }

    @Test
    void deleteStudentNegativeTest() {
        Optional<Student> actual = out.deleteStudent(-1L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getStudentWithAgeTest() {
        out.createStudent(STUDENT_3);
        out.createStudent(STUDENT_4);
        List<Student> actual = out.getStudentWithAge(20);
        List<Student> expected = new ArrayList<>(List.of(STUDENT_1, STUDENT_2));
        assertThat(actual).isEqualTo(expected);
    }
}