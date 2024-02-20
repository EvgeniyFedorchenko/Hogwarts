package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.exceptions.FacultyAlreadyExistsException;
import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalFacultyFieldsException;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
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
        constantsInitialisation();
    }

    @Test
    void createFacultyPositiveTest() {
        when(facultyRepositoryMock.existsByName(FACULTY_3.getName())).thenReturn(false);

        Faculty faculty3ForSaveMethod = new Faculty();
        faculty3ForSaveMethod.setName(FACULTY_3.getName());
        faculty3ForSaveMethod.setColor(FACULTY_3.getColor());
        faculty3ForSaveMethod.setStudents(FACULTY_3.getStudents());
        when(facultyRepositoryMock.save(faculty3ForSaveMethod)).thenReturn(FACULTY_3);

        Faculty actual = out.createFaculty(FACULTY_3);
        actual.setId(FACULTY_3.getId());
        actual.setStudents(FACULTY_3.getStudents());

        assertThat(actual).isEqualTo(FACULTY_3);
    }

    @Test
    void createFacultyWithInvalidParamsTest() {
        Faculty invalidFaculty = new Faculty();
        invalidFaculty.setId(null);
        invalidFaculty.setName(null);
        invalidFaculty.setColor(null);
        invalidFaculty.setStudents(null);

        assertThatThrownBy(() -> out.createFaculty(invalidFaculty))
                .isInstanceOf(IllegalFacultyFieldsException.class);
    }

    @Test
    void createFacultyWithAlreadyBeingName() {
        when(facultyRepositoryMock.existsByName(FACULTY_1.getName())).thenReturn(true);
        assertThatThrownBy(() -> out.createFaculty(FACULTY_1))
                .isInstanceOf(FacultyAlreadyExistsException.class);
    }


    @Test
    void getFacultyPositiveTest() {
        when(facultyRepositoryMock.findById(2L)).thenReturn(Optional.of(FACULTY_2));
        Faculty actual = out.findFaculty(2L).get();
        assertThat(actual).isEqualTo(FACULTY_2);
    }

    @Test
    void getFacultyWithNonexistentIdTest() {
        when(facultyRepositoryMock.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.findFaculty(3L).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyPositiveTest() {
        when(facultyRepositoryMock.findById(4L)).thenReturn(Optional.of(FACULTY_4));
        when(facultyRepositoryMock.save(FACULTY_4_EDITED)).thenReturn(FACULTY_4_EDITED);

        Faculty actual = out.updateFaculty(FACULTY_4.getId(), FACULTY_4_EDITED).get();
        assertTrue(actual.equals(FACULTY_4_EDITED));
    }

    @Test
    void updateFacultyWithNegativeIdTest() {
        when(facultyRepositoryMock.findById(-1L)).thenReturn(Optional.empty());

        Faculty invalidFaculty = new Faculty();
        invalidFaculty.setId(-1L);
        invalidFaculty.setName("faculty");
        invalidFaculty.setColor(Color.RED_GOLD);
        invalidFaculty.setStudents(new HashSet<>());

        assertThatThrownBy(
                () -> out.updateFaculty(invalidFaculty.getId(), invalidFaculty).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyWithNonexistentIdTest() {
        Faculty invalidFaculty = new Faculty();
        invalidFaculty.setId(100L);
        invalidFaculty.setName("faculty");
        invalidFaculty.setColor(Color.RED_GOLD);
        invalidFaculty.setStudents(new HashSet<>());

        when(facultyRepositoryMock.findById(invalidFaculty.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> out.updateFaculty(invalidFaculty.getId(), invalidFaculty).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateFacultyWithInvalidParamsTest() {
        Faculty invalidFaculty = new Faculty();
        invalidFaculty.setId(null);
        invalidFaculty.setName(null);
        invalidFaculty.setColor(null);
        invalidFaculty.setStudents(null);

        assertThatThrownBy(() -> out.updateFaculty(invalidFaculty.getId(), invalidFaculty))
                .isInstanceOf(IllegalFacultyFieldsException.class);
    }

    @Test
    void updateFacultyWithAlreadyBeingName() {

        Faculty invalidFaculty = new Faculty();
        invalidFaculty.setId(1L);
        invalidFaculty.setName("Hufflepuff");
        invalidFaculty.setColor(Color.RED_GOLD);
        invalidFaculty.setStudents(new HashSet<>());

        when(facultyRepositoryMock.findById(invalidFaculty.getId()))
                .thenReturn(Optional.of(invalidFaculty));
        when(facultyRepositoryMock.findFirstByName(invalidFaculty.getName())).thenReturn(FACULTY_2);

        assertThatThrownBy(() -> out.updateFaculty(invalidFaculty.getId(), invalidFaculty).get())
                .isInstanceOf(NoSuchElementException.class);
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

    /*@Test
    void getFacultyWithAgeTest() {
        when(facultyRepositoryMock.findFacultyByColorAndNameContainsIgnoreCase(Color.BLUE_BRONZE);
                .thenReturn(new ArrayList<>(List.of(FACULTY_3)));
        List<Faculty> actual = out.findFacultyByColorOrPartName(Color.BLUE_BRONZE, null);
        assertThat(actual).isEqualTo(new ArrayList<>(List.of(FACULTY_3)))
                .doesNotContainNull();

    }*/
}
