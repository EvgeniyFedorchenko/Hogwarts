package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.models.Student;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final Faculty FACULTY_1 = new Faculty(10L, "Gryffindor", Color.RED_GOLD);
    public static final Faculty FACULTY_2 = new Faculty(100500L, "Hufflepuff", Color.YELLOW_BLACK);
    public static final Faculty FACULTY_3 = new Faculty(0L, "Ravenclaw", Color.BLUE_BRONZE);
    public static final Faculty FACULTY_4 = new Faculty(4L, "Slytherin", Color.GREEN_SILVER);
    public static final Faculty FACULTY_4_EDITED = new Faculty(4L, "SlytherinEdited", Color.GREEN_SILVER);

    public static final List<Faculty> TEST_lIST_OF_4_FACULTY = new ArrayList<>(List.of(
            FACULTY_1, FACULTY_2, FACULTY_3, FACULTY_4));


    public static final Student STUDENT_1 = new Student(10L, "Student1", 20, 1L);
    public static final Student STUDENT_2 = new Student(10L, "Student2", 20, 2L);
    public static final Student STUDENT_3 = new Student(10L, "Student3", 21, 3L);
    public static final Student STUDENT_4 = new Student(10L, "Student4", 22, 4L);
    public static final Student STUDENT_4_EDITED = new Student(4L, "Student4edited", 23, 3L);

    public static final List<Student> TEST_lIST_OF_4_STUDENT = new ArrayList<>(List.of(
            STUDENT_1, STUDENT_2, STUDENT_3, STUDENT_4));
}
