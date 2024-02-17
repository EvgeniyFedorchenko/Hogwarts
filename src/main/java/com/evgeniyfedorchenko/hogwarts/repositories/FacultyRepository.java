package com.evgeniyfedorchenko.hogwarts.repositories;

import com.evgeniyfedorchenko.hogwarts.models.Color;
import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    boolean existsByName(String name);

    List<Faculty> findByColor(Color color);

    List<Faculty> findFacultyByColorOrNameContainsIgnoreCase(Color color, String name);
}
