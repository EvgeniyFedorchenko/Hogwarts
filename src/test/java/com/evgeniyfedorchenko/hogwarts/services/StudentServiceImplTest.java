package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.FacultyNotFoundException;
import com.evgeniyfedorchenko.hogwarts.exceptions.parentProjectException.IllegalStudentFieldsException;
import com.evgeniyfedorchenko.hogwarts.repositories.FacultyRepository;
import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.evgeniyfedorchenko.hogwarts.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepositoryMock;
    @Mock
    private FacultyRepository facultyRepositoryMock;
    @Mock
    private FacultyService facultyServiceMock;
    @InjectMocks
    private StudentServiceImpl out;

    @BeforeEach
    void BeforeEach() {
        testConstantsInitialisation();
    }

    @Test
    void createStudentPositiveTest() {
        STUDENT_3.setFaculty(FACULTY_1);
        Student incompleteStudent = new Student();
        incompleteStudent.setName(STUDENT_3.getName());
        incompleteStudent.setAge(STUDENT_3.getAge());
        incompleteStudent.setFaculty(STUDENT_3.getFaculty());

        when(facultyRepositoryMock.findById(FACULTY_1.getId())).thenReturn(Optional.of(FACULTY_1));
        when(studentRepositoryMock.save(incompleteStudent)).thenReturn(STUDENT_3);
        when(facultyServiceMock.updateFaculty(FACULTY_1.getId(), FACULTY_1)).thenReturn(Optional.of(FACULTY_3));

        Student actual = out.createStudent(STUDENT_3);
        assertThat(actual)
                .isEqualTo(STUDENT_3);
    }

    @Test
    void createStudentWithoutNameTest() {
        Student studentWithoutAge = STUDENT_1;
        studentWithoutAge.setAge(0);

        assertThatThrownBy(() -> out.createStudent(studentWithoutAge))
                .isInstanceOf(IllegalStudentFieldsException.class);
    }

    @Test
    void createStudentWithoutAgeTest() {
        Student studentWithoutName = STUDENT_1;
        studentWithoutName.setName(null);

        assertThatThrownBy(() -> out.createStudent(studentWithoutName))
                .isInstanceOf(IllegalStudentFieldsException.class);
    }

    @Test
    void getStudentPositiveTest() {
        when(studentRepositoryMock.findById(2L)).thenReturn(Optional.of(STUDENT_2));
        Student actual = out.findStudent(2L).get();
        assertThat(actual).isEqualTo(STUDENT_2);
    }

    @Test
    void getStudentWithNonexistentIdTest() {
        when(studentRepositoryMock.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> out.findStudent(3L).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateStudentPositiveTest() {

        STUDENT_4_EDITED.setFaculty(FACULTY_1);
        when(studentRepositoryMock.findById(STUDENT_4.getId())).thenReturn(Optional.of(STUDENT_4));
        when(studentRepositoryMock.save(STUDENT_4_EDITED)).thenReturn(STUDENT_4_EDITED);
        when(facultyRepositoryMock.findById(STUDENT_4_EDITED.getFaculty().getId()))
                .thenReturn(Optional.of(STUDENT_4_EDITED.getFaculty()));

        Student actual = out.updateStudent(STUDENT_4_EDITED.getId(), STUDENT_4_EDITED).get();
        assertTrue(actual.equals(STUDENT_4_EDITED));
    }

    @Test
    void updateStudentWithNegativeIdTest() {
        when(studentRepositoryMock.findById(-1L)).thenReturn(Optional.empty());

        Student invalidStudent = new Student();
        invalidStudent.setId(-1L);
        invalidStudent.setName("student");
        invalidStudent.setAge(18);
        invalidStudent.setFaculty(FACULTY_4);

        assertThatThrownBy(
                () -> out.updateStudent(invalidStudent.getId(), invalidStudent).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateStudentWithNonexistentIdTest() {
        when(studentRepositoryMock.findById(100L)).thenReturn(Optional.empty());

        Student invalidStudent = new Student();
        invalidStudent.setId(100L);
        invalidStudent.setName("student");
        invalidStudent.setAge(18);
        invalidStudent.setFaculty(FACULTY_4);

        assertThatThrownBy(
                () -> out.updateStudent(invalidStudent.getId(), invalidStudent).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateStudentWithInvalidParamsTest() {
        assertThatThrownBy(() -> out.updateStudent(null, new Student()))
                .isInstanceOf(IllegalStudentFieldsException.class);
    }

    @Test
    void updateStudentWithoutFacultyTest() {
        when(studentRepositoryMock.findById(STUDENT_WITHOUT_FACULTY.getId()))
                .thenReturn(Optional.of(STUDENT_WITHOUT_FACULTY));
        assertThatThrownBy(() -> out.updateStudent(STUDENT_WITHOUT_FACULTY.getId(), STUDENT_WITHOUT_FACULTY).get())
                .isInstanceOf(IllegalStudentFieldsException.class);

    }

    @Test
    void updateStudentWithInvalidFaculty() {
        Student studentWithInvalidFacultyId = new Student();
        studentWithInvalidFacultyId.setId(1L);
        studentWithInvalidFacultyId.setName("studentWithInvalidFacultyId");
        studentWithInvalidFacultyId.setAge(20);

        Faculty facultyWithInvalidId = new Faculty();
        facultyWithInvalidId.setId(Long.MAX_VALUE);
        facultyWithInvalidId.setName("facultyWithInvalidId");
        facultyWithInvalidId.setColor(Color.RED_GOLD);

        studentWithInvalidFacultyId.setFaculty(facultyWithInvalidId);

        when(studentRepositoryMock.findById(studentWithInvalidFacultyId.getId()))
                .thenReturn(Optional.of(studentWithInvalidFacultyId));
        when(facultyRepositoryMock.findById(facultyWithInvalidId.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> out.updateStudent(studentWithInvalidFacultyId.getId(), studentWithInvalidFacultyId))
                .isInstanceOf(FacultyNotFoundException.class);

    }

    @Test
    void findStudentsByExactAgeTest() {
        when(studentRepositoryMock.findByAge(STUDENT_1.getAge())).thenReturn(List.of(STUDENT_1));
        List<Student> actual = out.findStudentsByAgeBetween(STUDENT_1.getAge(), -1);
        assertThat(actual).doesNotContainNull()
                .containsOnly(STUDENT_1);
    }

    @Test
    void findStudentsByAgeBetweenTest() {
        when(studentRepositoryMock.findByAgeBetween(STUDENT_1.getAge(), STUDENT_3.getAge()))
                .thenReturn(List.of(STUDENT_1, STUDENT_2, STUDENT_3));

        List<Student> actual = out.findStudentsByAgeBetween(STUDENT_1.getAge(), STUDENT_3.getAge());
        assertThat(actual).doesNotContainNull()
                .containsOnly(STUDENT_1, STUDENT_2, STUDENT_3);
    }

    @Test
    void findFacultyPositiveTest() {
        STUDENT_1.setFaculty(FACULTY_1);
        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.of(STUDENT_1));
        Optional<Faculty> actual = out.findFaculty(STUDENT_1.getId());
        assertThat(actual.get()).isEqualTo(STUDENT_1.getFaculty());
    }

    @Test
    void findFacultyNegativeTest() {
        when(studentRepositoryMock.findById(STUDENT_1.getId())).thenReturn(Optional.empty());
        Optional<Faculty> actual = out.findFaculty(STUDENT_1.getId());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    void deleteStudentTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(STUDENT_1));
        assertThat(out.deleteStudent(anyLong()).get()).isEqualTo(STUDENT_1);
    }

    @Test
    void deleteStudentWithNonexistentIdTest() {
        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        assertThat(out.deleteStudent(anyLong())).isEqualTo(Optional.empty());
        assertThatThrownBy(() -> out.deleteStudent(anyLong()).get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteStudentNegativeTest() {
        Optional<Student> actual = out.deleteStudent(-1L);
        assertThat(actual).isEqualTo(Optional.empty());
        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
    }
}