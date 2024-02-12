package com.evgeniyfedorchenko.hogwarts.repositories;

import com.evgeniyfedorchenko.hogwarts.models.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByNameLike(String name);
}
