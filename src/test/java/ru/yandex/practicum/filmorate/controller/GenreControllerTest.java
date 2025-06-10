package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GenreControllerTest {

    public static final String PATH = "/genres";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Autowired
    private ObjectMapper objectMapper;

    private Genre testGenre;

    @BeforeEach
    void setUp() {
        testGenre = Genre.builder()
                .id(1L)
                .name("Комедия")
                .build();
    }

    @Test
    void whenGetAllGenres() throws Exception {
        List<Genre> genres = List.of(testGenre);
        when(genreService.getAllGenres()).thenReturn(genres);

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testGenre.getId()))
                .andExpect(jsonPath("$[0].name").value(testGenre.getName()));

        verify(genreService, times(1)).getAllGenres();
    }

    @Test
    void whenGetGenre() throws Exception {
        Long genreId = 1L;
        when(genreService.getGenre(genreId)).thenReturn(testGenre);

        mockMvc.perform(get(PATH + "/{id}", genreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testGenre.getId()))
                .andExpect(jsonPath("$.name").value(testGenre.getName()));

        verify(genreService, times(1)).getGenre(genreId);
    }

}