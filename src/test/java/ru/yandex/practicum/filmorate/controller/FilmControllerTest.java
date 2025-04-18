package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class FilmControllerTest {

    public static final String PATH = "/films";

    @Autowired
    private MockMvc mockMvc;
    FilmController filmController;


    @Test
    void create() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("films/film.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(getContentFromFile("films/film.json")));
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("films/film.json")));

        mockMvc.perform(MockMvcRequestBuilders.put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("films/updateFilm.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(getContentFromFile("films/updateFilm.json")));
    }

    @Test
    void getAllFilms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("films/film.json")));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    private String getContentFromFile(String filename) throws IOException {

        return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(), StandardCharsets.UTF_8);
    }
}