package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    @EqualsAndHashCode.Include
    private Long id;

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;
}
