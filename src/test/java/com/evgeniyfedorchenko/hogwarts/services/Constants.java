package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import com.evgeniyfedorchenko.hogwarts.entities.Student;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Constants {

    public static final Faculty FACULTY_1 = new Faculty();
    public static final Faculty FACULTY_2 = new Faculty();
    public static final Faculty FACULTY_3 = new Faculty();
    public static final Faculty FACULTY_4 = new Faculty();
    public static final Faculty FACULTY_4_EDITED = new Faculty();


    public static final Student STUDENT_1 = new Student();
    public static final Student STUDENT_2 = new Student();
    public static final Student STUDENT_3 = new Student();
    public static final Student STUDENT_4 = new Student();
    public static final Student STUDENT_4_EDITED = new Student();


    public static final List<Faculty> TEST_lIST_OF_4_FACULTY = new ArrayList<>(List.of(
            FACULTY_1, FACULTY_2, FACULTY_3, FACULTY_4));
    public static final List<Student> TEST_lIST_OF_4_STUDENT = new ArrayList<>(List.of(
            STUDENT_1, STUDENT_2, STUDENT_3, STUDENT_4));

    public static void constantsInitialisation() {

        FACULTY_1.setId(1L);
        FACULTY_1.setName("Gryffindor");
        FACULTY_1.setColor(Color.RED_GOLD);
        FACULTY_1.setStudents(new HashSet<>());

        FACULTY_2.setId(2L);
        FACULTY_2.setName("Hufflepuff");
        FACULTY_2.setColor(Color.YELLOW_BLACK);
        FACULTY_2.setStudents(new HashSet<>());

        FACULTY_3.setId(3L);
        FACULTY_3.setName("Ravenclaw");
        FACULTY_3.setColor(Color.BLUE_BRONZE);
        FACULTY_3.setStudents(new HashSet<>());

        FACULTY_4.setId(4L);
        FACULTY_4.setName("Slytherin");
        FACULTY_4.setColor(Color.GREEN_SILVER);
        FACULTY_4.setStudents(new HashSet<>());

        FACULTY_4_EDITED.setId(4L);
        FACULTY_4_EDITED.setName("SlytherinEdited");
        FACULTY_4_EDITED.setColor(Color.BLUE_BRONZE);
        FACULTY_4_EDITED.setStudents(new HashSet<>());


        STUDENT_1.setId(1L);
        STUDENT_1.setName("Student1");
        STUDENT_1.setAge(20);
        STUDENT_1.setFaculty(FACULTY_1);

        STUDENT_2.setId(2L);
        STUDENT_2.setName("Student2");
        STUDENT_2.setAge(21);
        STUDENT_2.setFaculty(FACULTY_2);

        STUDENT_3.setId(3L);
        STUDENT_3.setName("Student3");
        STUDENT_3.setAge(22);
        STUDENT_3.setFaculty(FACULTY_3);

        STUDENT_4.setId(4L);
        STUDENT_4.setName("Student4");
        STUDENT_4.setAge(23);
        STUDENT_4.setFaculty(FACULTY_4);

        STUDENT_4_EDITED.setId(4L);
        STUDENT_4_EDITED.setName("Student4Edited");
        STUDENT_4_EDITED.setAge(24);
        STUDENT_4_EDITED.setFaculty(FACULTY_4);
    }
}
