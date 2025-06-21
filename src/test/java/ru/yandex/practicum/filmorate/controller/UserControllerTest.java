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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private User friendUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .login("userLogin")
                .name("User Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .frendSet(new HashSet<>())
                .build();

        friendUser = User.builder()
                .id(2L)
                .email("friend@example.com")
                .login("friendLogin")
                .name("Friend Name")
                .birthday(LocalDate.of(1992, 2, 2))
                .frendSet(new HashSet<>())
                .build();
    }


    @Test
    void whenCreateUser() throws Exception {
        when(userService.create(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.login").value(testUser.getLogin()));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void whenUpdateUser() throws Exception {
        when(userService.update(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value(testUser.getName()));

        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    void whenGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login").value(testUser.getLogin()));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void whenAddFriend_shouldReturnUserWithFriend() throws Exception {
        when(userService.addFriend(1L, 2L)).thenReturn(testUser);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()));

        verify(userService, times(1)).addFriend(1L, 2L);
    }

    @Test
    void whenGetFriends() throws Exception {
        when(userService.getFriends(1L)).thenReturn(Set.of(friendUser));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(friendUser.getId()));

        verify(userService, times(1)).getFriends(1L);
    }

    @Test
    void whenDeleteFriend() throws Exception {
        when(userService.deleteFriend(1L, 2L)).thenReturn(testUser);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()));

        verify(userService, times(1)).deleteFriend(1L, 2L);
    }

    @Test
    void whenGetMutualFriends_shouldReturnCommonFriends() throws Exception {
        User commonFriend = new User();
        commonFriend.setId(3L);
        commonFriend.setLogin("commonFriend");

        when(userService.mutualFriends(1L, 2L)).thenReturn(Set.of(commonFriend));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(commonFriend.getId()));

        verify(userService, times(1)).mutualFriends(1L, 2L);
    }
}