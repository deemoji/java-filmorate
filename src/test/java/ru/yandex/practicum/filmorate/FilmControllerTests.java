package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateFilm() throws Exception {
        Film film = new Film();
        film.setName("Test film");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void shouldFailValidationWhenEmptyRequest() throws Exception {
        Film film = new Film();

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
