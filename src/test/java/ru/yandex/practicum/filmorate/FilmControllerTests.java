package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateFilm() throws Exception {
        NewFilmRequest film = new NewFilmRequest();
        film.setName("Test film");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        ResponseEntity<FilmDto> response = restTemplate.postForEntity("/films", film, FilmDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void shouldFailValidationWhenEmptyRequest() throws Exception {
        FilmDto film = new FilmDto();

        ResponseEntity<FilmDto> response = restTemplate.postForEntity("/films", film, FilmDto.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
