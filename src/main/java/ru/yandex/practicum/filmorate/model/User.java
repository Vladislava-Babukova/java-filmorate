package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    String name;
    private long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private LocalDate birthday;
    private Set<Long> frendSet = new HashSet<>();
}