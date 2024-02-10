package com.evgeniyfedorchenko.hogwarts.services;

import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    private final Map<Long, Faculty> faculties;
    private Long countId = 0L;

    public FacultyServiceImpl(Map<Long, Faculty> faculties) {
        this.faculties = faculties;
    }

    public Faculty createFaculty(String name, Color color) {
        Faculty faculty = new Faculty(++countId, name, color);
        return createFaculty(faculty);
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        if (faculties.containsValue(faculty)) {   // Двух Гриффиндоров под разными id не должно существовать
            return faculty;                             // см. Faculty.equals()
        }
        faculty.setId(++countId);
        return faculties.put(faculty.getId(), faculty);
    }

    @Override
    public Optional<Faculty> getFaculty(Long id) {
        return Optional.ofNullable(faculties.get(id));
    }

    @Override
    public Optional<Faculty> updateFaculty(Long id, Faculty faculty) {
        return (faculties.containsKey(id) && faculty != null)
                ? Optional.of(faculties.put(id, faculty))
                : Optional.empty();
    }

    @Override
    public Optional<Faculty> deleteFaculty(Long id) {
        return (faculties.containsKey(id)) ? Optional.of(faculties.remove(id)) : Optional.empty();
    }
}
