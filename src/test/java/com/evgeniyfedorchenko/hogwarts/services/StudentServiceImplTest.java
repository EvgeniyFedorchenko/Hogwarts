//package com.evgeniyfedorchenko.hogwarts.services;
//
//import com.evgeniyfedorchenko.hogwarts.exceptions.IllegalStudentFieldsException;
//import com.evgeniyfedorchenko.hogwarts.models.Student;
//import com.evgeniyfedorchenko.hogwarts.repositories.StudentRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import static com.evgeniyfedorchenko.hogwarts.services.Constants.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class StudentServiceImplTest {
//
//    @Mock
//    private StudentRepository studentRepositoryMock;
//    @InjectMocks
//    private StudentServiceImpl out;
//
//    @Test
//    void createStudentPositiveTest() {
//        when(studentRepositoryMock.save(STUDENT_3)).thenReturn(STUDENT_3);
//        Student actual = out.createStudent(STUDENT_3);
//        assertThat(actual).isEqualTo(STUDENT_3);
//    }
//
//    @Test
//    void createStudentWithInvalidParamsTest() {
//        assertThatThrownBy(() -> out.createStudent(new Student(null, null, 0, null)))
//                .isInstanceOf(IllegalStudentFieldsException.class);
//    }
//
//    @Test
//    void getStudentPositiveTest() {
//        when(studentRepositoryMock.findById(2L)).thenReturn(Optional.of(STUDENT_2));
//        Student actual = out.findStudent(2L).get();
//        assertThat(actual).isEqualTo(STUDENT_2);
//    }
//
//    @Test
//    void getStudentWithNonexistentIdTest() {
//        when(studentRepositoryMock.findById(3L)).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> out.findStudent(3L).get())
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void updateStudentPositiveTest() {
//        when(studentRepositoryMock.findById(4L)).thenReturn(Optional.of(STUDENT_4));
//        when(studentRepositoryMock.save(STUDENT_4_EDITED)).thenReturn(STUDENT_4_EDITED);
//
//        Student actual = out.updateStudent(STUDENT_4_EDITED).get();
//        assertTrue(actual.equals(STUDENT_4_EDITED));
//    }
//
//    @Test
//    void updateStudentWithNegativeIdTest() {
//        when(studentRepositoryMock.findById(-1L)).thenReturn(Optional.empty());
//        assertThatThrownBy(
//                () -> out.updateStudent(new Student(-1L, "student", 18, 4L)).get())
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void updateStudentWithNonexistentIdTest() {
//        when(studentRepositoryMock.findById(100L)).thenReturn(Optional.empty());
//        assertThatThrownBy(
//                () -> out.updateStudent(new Student(100L, "student", 18, 4L)).get())
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void updateStudentWithInvalidParamsTest() {
//        assertThatThrownBy(() -> out.updateStudent(new Student(null, null, 0, null)))
//                .isInstanceOf(IllegalStudentFieldsException.class);
//    }
//
//    @Test
//    void deleteStudentTest() {
//        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(STUDENT_1));
//        assertThat(out.deleteStudent(anyLong()).get()).isEqualTo(STUDENT_1);
//    }
//
//    @Test
//    void deleteStudentWithNonexistentIdTest() {
//        when(studentRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
//        assertThat(out.deleteStudent(anyLong())).isEqualTo(Optional.empty());
//        assertThatThrownBy(() -> out.deleteStudent(anyLong()).get())
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void deleteStudentNegativeTest() {
//        Optional<Student> actual = out.deleteStudent(-1L);
//        assertThat(actual).isEqualTo(Optional.empty());
//        assertThatThrownBy(actual::get).isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void getStudentWithAgeTest() {
//        when(studentRepositoryMock.findAll()).thenReturn(TEST_lIST_OF_4_STUDENT);
//        List<Student> actual = out.findStudentsByExactAge(20);
//        assertThat(actual).isEqualTo(new ArrayList<>(List.of(STUDENT_1, STUDENT_2)))
//                .doesNotContainNull();
//
//    }
//}