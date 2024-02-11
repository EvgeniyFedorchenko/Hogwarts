package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import com.evgeniyfedorchenko.hogwarts.models.Student;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final Map<Long, Faculty> faculties;
    private Long countId = 0L;

    public FacultyServiceImpl() {
        this.faculties = new HashMap<>();
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        if (faculties.containsValue(faculty)) {   // Двух Гриффиндоров под разными id не должно существовать
            return faculty;                             // см. Faculty.equals()
        }
        faculty.setId(++countId);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    @Override
    public Optional<Faculty> getFaculty(Long id) {
        return Optional.ofNullable(faculties.get(id));
    }

    @Override
    public Optional<Faculty> updateFaculty(Long id, Faculty faculty) {
        if (faculties.containsKey(id) && faculty != null) {
            Faculty old = faculties.get(id);
            faculties.replace(id, faculty);
            return Optional.of(old);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Faculty> deleteFaculty(Long id) {
        return (faculties.containsKey(id)) ? Optional.of(faculties.remove(id)) : Optional.empty();
    }

    @Override
    public List<Faculty> getFacultyWithColor(Color color) {
        return faculties.values().stream()
                .filter(faculty -> faculty.getColor() == color)
                .collect(Collectors.toList());
    }
}
