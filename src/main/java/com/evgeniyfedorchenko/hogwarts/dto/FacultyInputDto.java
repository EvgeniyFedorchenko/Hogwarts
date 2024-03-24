package com.evgeniyfedorchenko.hogwarts.dto;


import com.evgeniyfedorchenko.hogwarts.entities.Color;
import jakarta.validation.constraints.NotBlank;

public class FacultyInputDto {

    @NotBlank(message = "Faculty name cannot be empty")
    private String name;

    @NotBlank(message = "Faculty color cannot be empty")
    private Color color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
