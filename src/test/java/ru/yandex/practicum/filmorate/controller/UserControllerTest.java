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
class UserControllerTest {

    public static final String PATH = "/users";

    @Autowired
    private MockMvc mockMvc;
    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("users/user.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(getContentFromFile("users/user.json")));
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("users/user.json")));

        mockMvc.perform(MockMvcRequestBuilders.put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("users/updateUser.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(getContentFromFile("users/updateUser.json")));
    }

    @Test
    void getAllFilms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("users/user.json")));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    private String getContentFromFile(String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("Не открывается файл", exception);
        }
    }
}