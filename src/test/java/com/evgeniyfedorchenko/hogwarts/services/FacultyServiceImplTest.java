package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.evgeniyfedorchenko.hogwarts.services.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FacultyServiceImplTest {

    private final FacultyServiceImpl out = new FacultyServiceImpl();

    @BeforeEach
    public void beforeEach() {
        out.createFaculty(FACULTY_1);
        out.createFaculty(FACULTY_2);
    }

    @AfterEach
    public void afterEach() {
        Long id = 1L;
        while (out.getFaculty(id).isPresent()) {
            out.deleteFaculty(id);
        }
    }

    @Test
    void createFacultyPositiveTest() {
        Faculty actual = out.createFaculty(FACULTY_3);
        assertThat(actual).isEqualTo(FACULTY_3);
        assertThat(actual.getId()).isEqualTo(3L);
        assertThat(out.getFaculty(3L).get()).isEqualTo(FACULTY_3);
    }

    @Test
    void createFacultyNegativeTest() {
        out.createFaculty(FACULTY_1);
        Optional<Faculty> actual = out.getFaculty(3L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getFacultyPositiveTest() {
        Faculty actual = out.getFaculty(2L).get();
        assertThat(actual).isEqualTo(FACULTY_2);
    }

    @Test
    void getFacultyNegativeTest() {
        Optional<Faculty> actual = out.getFaculty(3L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyPositiveTest() {
        Faculty actual = out.updateFaculty(2L, FACULTY_4).get();
        assertThat(actual).isEqualTo(FACULTY_2);
    }

    @Test
    void updateFacultyNegativeTest1() {
        Optional<Faculty> actual = out.updateFaculty(-1L, FACULTY_4);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyNegativeTest2() {
        Optional<Faculty> actual = out.updateFaculty(2L, null);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteFacultyTest() {
        Faculty actual = out.deleteFaculty(2L).get();
        assertThat(actual).isEqualTo(FACULTY_2);
    }

    /*@Test
    void deleteFacultyNegativeTest() {
        Optional<Faculty> actual = out.deleteFaculty(-1L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }*/

    @Test
    void getFacultyWithColor() {
        out.createFaculty(FACULTY_3);
        out.createFaculty(FACULTY_4);
        List<Faculty> actual = out.getFacultyWithColor(Color.RED_GOLD);
        List<Faculty> expected = new ArrayList<>(List.of(FACULTY_1));
        assertThat(actual).isEqualTo(expected);
    }
}