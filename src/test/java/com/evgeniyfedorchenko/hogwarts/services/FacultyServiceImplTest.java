package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.evgeniyfedorchenko.hogwarts.services.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {

    @Mock
    private FacultyRepository facultyRepositoryMock;
    @InjectMocks
    private FacultyServiceImpl out;

    @BeforeEach
    public void beforeEach() {
        when(facultyRepositoryMock.save(FACULTY_1)).thenReturn(FACULTY_1);
        when(facultyRepositoryMock.save(FACULTY_2)).thenReturn(FACULTY_2);
        out.createFaculty(FACULTY_1);
        out.createFaculty(FACULTY_2);
    }

    @Test
    void createFacultyPositiveTest() {
        when(facultyRepositoryMock.save(FACULTY_3)).thenReturn(FACULTY_3);
        Faculty actual = out.createFaculty(FACULTY_3);
        assertThat(actual).isEqualTo(FACULTY_3);
    }

    @Test
    void createFacultyWithInvalidParamsTest() {
        assertThatThrownBy(() -> out.createFaculty(new Faculty(null, null, null)))
                .isInstanceOf(IllegalFacultyFieldsException.class);
    }

    @Test
    void getFacultyPositiveTest() {
        when(facultyRepositoryMock.findById(2L)).thenReturn(Optional.of(FACULTY_2));
        Faculty actual = out.getFaculty(2L).get();
        assertThat(actual).isEqualTo(FACULTY_2);
    }

    @Test
    void getFacultyWithNonexistentIdTest() {
        when(facultyRepositoryMock.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.getFaculty(3L).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyPositiveTest() {
        when(facultyRepositoryMock.findById(4L)).thenReturn(Optional.of(FACULTY_4));
        when(facultyRepositoryMock.save(FACULTY_4_EDITED)).thenReturn(FACULTY_4_EDITED);

        Faculty actual = out.updateFaculty(FACULTY_4_EDITED).get();
        assertTrue(actual.equals(FACULTY_4_EDITED));
    }

    @Test
    void updateFacultyWithNegativeIdTest() {
        when(facultyRepositoryMock.findById(-1L)).thenReturn(Optional.empty());
        assertThatThrownBy(
                () -> out.updateFaculty(new Faculty(-1L, "faculty", Color.BLUE_BRONZE)).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyWithNonexistentIdTest() {
        when(facultyRepositoryMock.findById(100L)).thenReturn(Optional.empty());
        assertThatThrownBy(
                () -> out.updateFaculty(new Faculty(100L, "faculty", Color.RED_GOLD)).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyWithInvalidParamsTest() {
        assertThatThrownBy(() -> out.updateFaculty(new Faculty(null, null, null)))
                .isInstanceOf(IllegalFacultyFieldsException.class);
    }

    @Test
    void updateFacultyWithAlreadyBeingName() {
        when(facultyRepositoryMock.findByNameLike("Gryffindor")).thenReturn(new ArrayList<>(List.of(FACULTY_1)));

        assertThatThrownBy(() -> out.updateFaculty(new Faculty(1L, "Gryffindor", Color.RED_GOLD)))
                .isInstanceOf(IllegalFacultyFieldsException.class);
    }

    @Test
    void deleteFacultyTest() {
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.of(FACULTY_1));
        assertThat(out.deleteFaculty(anyLong()).get()).isEqualTo(FACULTY_1);
    }

    @Test
    void deleteFacultyWithNonexistentIdTest() {
        when(facultyRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        assertThat(out.deleteFaculty(anyLong())).isEqualTo(Optional.empty());
        assertThatThrownBy(() -> out.deleteFaculty(anyLong()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteFacultyNegativeTest() {
        Optional<Faculty> actual = out.deleteFaculty(-1L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getFacultyWithAgeTest() {
        when(facultyRepositoryMock.findAll()).thenReturn(TEST_lIST_OF_4_FACULTY);
        List<Faculty> actual = out.getFacultyWithColor(Color.BLUE_BRONZE);
        assertThat(actual).isEqualTo(new ArrayList<>(List.of(FACULTY_3)))
                .doesNotContainNull();

    }
}