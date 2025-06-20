package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MpaControllerTest {

    private static final String PATH = "/mpa";
    private static final Mpa TEST_MPA = Mpa.builder()
            .id(1L)
            .name("PG-13")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;


    @Test
    void whenGetAllMpa() throws Exception {
        List<Mpa> mpaList = List.of(TEST_MPA);
        when(mpaService.getAllMpa()).thenReturn(mpaList);

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(TEST_MPA.getId()))
                .andExpect(jsonPath("$[0].name").value(TEST_MPA.getName()));

        verify(mpaService, times(1)).getAllMpa();
    }

    @Test
    void whenGetMpaById() throws Exception {
        when(mpaService.getMpa(1L)).thenReturn(TEST_MPA);

        mockMvc.perform(get(PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(TEST_MPA.getId()))
                .andExpect(jsonPath("$.name").value(TEST_MPA.getName()));

        verify(mpaService, times(1)).getMpa(1L);
    }

}