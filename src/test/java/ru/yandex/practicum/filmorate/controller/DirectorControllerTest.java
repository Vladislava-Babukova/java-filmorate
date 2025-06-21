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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class DirectorControllerTest {


    public static final String PATH = "/directors";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectorService directorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Director testDirector;

    @BeforeEach
    void setUp() {
        testDirector = Director.builder()
                .id(1L)
                .name("Christopher Nolan")
                .build();
    }

    @Test
    void whenCreate() throws Exception {
        when(directorService.create(any(Director.class))).thenReturn(testDirector);

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDirector)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Christopher Nolan"));

        verify(directorService, times(1)).create(any(Director.class));
    }

    @Test
    void whenCreateNullDirector() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetDirector() throws Exception {
        when(directorService.getDirector(1L)).thenReturn(testDirector);

        mockMvc.perform(get(PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Christopher Nolan"));
    }


    @Test
    void whenGetAll() throws Exception {
        List<Director> directors = List.of(testDirector);
        when(directorService.getAll()).thenReturn(directors);

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Christopher Nolan"));
    }

    @Test
    void whenUpdate() throws Exception {
        Director updatedDirector = Director.builder()
                .id(1L)
                .name("Christopher Nolan Updated")
                .build();

        when(directorService.update(any(Director.class))).thenReturn(updatedDirector);

        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDirector)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Christopher Nolan Updated"));
    }

    @Test
    void whenUpdateNullDirector() throws Exception {
        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDelete() throws Exception {
        when(directorService.delete(1L)).thenReturn(testDirector);

        mockMvc.perform(delete(PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}