package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.time.LocalDate;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FilmControllerTest {

    public static final String PATH = "/films";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = Film.builder()
                .id(1L)
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120L)
                .build();
    }

    @Test
    void whenCreateFilm() throws Exception {
        when(filmService.create(any(Film.class))).thenReturn(testFilm);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFilm.getId()))
                .andExpect(jsonPath("$.name").value(testFilm.getName()));

        verify(filmService, times(1)).create(any(Film.class));
    }

    @Test
    void whenUpdateFilm() throws Exception {
        when(filmService.update(any(Film.class))).thenReturn(testFilm);

        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFilm.getId()))
                .andExpect(jsonPath("$.name").value(testFilm.getName()));

        verify(filmService, times(1)).update(any(Film.class));
    }

    @Test
    void whenGetAllFilms() throws Exception {
        List<Film> films = List.of(testFilm);
        when(filmService.getAllFilms()).thenReturn(films);

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testFilm.getId()));

        verify(filmService, times(1)).getAllFilms();
    }

    @Test
    void whenGetTopFilms() throws Exception {
        List<Film> topFilms = List.of(testFilm);
        int count = 10;
        when(filmService.topFilms(count)).thenReturn(topFilms);

        mockMvc.perform(get(PATH + "/popular?count={count}", count))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testFilm.getId()));

        verify(filmService, times(1)).topFilms(count);
    }

    @Test
    void whenAddLike() throws Exception {
        Long filmId = 1L;
        Long userId = 1L;
        when(filmService.addLike(filmId, userId)).thenReturn(testFilm);

        mockMvc.perform(put(PATH + "/{film_id}/like/{user_Id}", filmId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFilm.getId()));

        verify(filmService, times(1)).addLike(filmId, userId);
    }

    @Test
    void whenDeleteLike() throws Exception {
        Long filmId = 1L;
        Long userId = 1L;
        when(filmService.deleteLike(filmId, userId)).thenReturn(testFilm);

        mockMvc.perform(delete(PATH + "/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFilm.getId()));

        verify(filmService, times(1)).deleteLike(filmId, userId);
    }

    @Test
    void whenGetFilm() throws Exception {
        Long filmId = 1L;
        when(filmService.getFilm(filmId)).thenReturn(testFilm);

        mockMvc.perform(get(PATH + "/{id}", filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFilm.getId()))
                .andExpect(jsonPath("$.name").value(testFilm.getName()));

        verify(filmService, times(1)).getFilm(filmId);
    }
}