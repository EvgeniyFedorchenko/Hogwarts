package com.evgeniyfedorchenko.hogwarts.repositories;

import com.evgeniyfedorchenko.hogwarts.entities.Color;
import com.evgeniyfedorchenko.hogwarts.entities.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    boolean existsByName(String name);

    List<Faculty> findFacultyByColorAndNameContainsIgnoreCase(Color color, String name);

    List<Faculty> findByNameContainsIgnoreCase(String name);

    Optional<Faculty> findFirstByName(String name);
}
