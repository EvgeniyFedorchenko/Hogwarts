package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.models.Student;

public class Constants {

    public static final Faculty FACULTY_1 = new Faculty(10L, "Gryffindor", Color.RED_GOLD);
    public static final Faculty FACULTY_2 = new Faculty(100500L, "Hufflepuff", Color.YELLOW_BLACK);
    public static final Faculty FACULTY_3 = new Faculty(0L, "Ravenclaw", Color.BLUE_BRONZE);
    public static final Faculty FACULTY_4 = new Faculty(-6L, "Slytherin", Color.GREEN_SILVER);

    public static final Student STUDENT_1 = new Student(10L, "Student1", 20, 1L);
    public static final Student STUDENT_2 = new Student(10L, "Student2", 20, 2L);
    public static final Student STUDENT_3 = new Student(10L, "Student3", 21, 3L);
    public static final Student STUDENT_4 = new Student(10L, "Student4", 22, 4L);
}
