package org.example.shvidkiyhomework_int4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Viktor Shvidkiy
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsersShouldReturnList() throws Exception {
        String time1 = LocalDateTime.now().minusMinutes(10).toString();
        String time2 = LocalDateTime.now().toString();
        List<UserDto> users = List.of(
                UserDto.builder()
                        .id(1)
                        .name("Jack")
                        .email("jack@gmail.com")
                        .age(30)
                        .createdAt(time1)
                        .build(),
                UserDto.builder()
                        .id(2)
                        .name("Bill")
                        .email("bill@gmail.com")
                        .age(35)
                        .createdAt(time2)
                        .build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Jack"))
                .andExpect(jsonPath("$[0].email").value("jack@gmail.com"))
                .andExpect(jsonPath("$[0].age").value("30"))
                .andExpect(jsonPath("$[0].createdAt").value(time1))
                .andExpect(jsonPath("$[1].name").value("Bill"))
                .andExpect(jsonPath("$[1].email").value("bill@gmail.com"))
                .andExpect(jsonPath("$[1].age").value("35"))
                .andExpect(jsonPath("$[1].createdAt").value(time2));
    }

    @Test
    void getUserByIdFound() throws Exception {
        String time = LocalDateTime.now().toString();
        UserDto dto = UserDto.builder()
                .id(1)
                .name("Bob")
                .email("bob@gmail.com")
                .age(40)
                .createdAt(time)
                .build();

        when(userService.getUserById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@gmail.com"))
                .andExpect(jsonPath("$.age").value("40"))
                .andExpect(jsonPath("$.createdAt").value(time));
    }

    @Test
    void getUserByIdNotFound() throws Exception{
        when(userService.getUserById(100)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserSuccess() throws Exception{
        String time = LocalDateTime.now().toString();
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("Tom")
                .email("tom@gmail.com")
                .age(20)
                .build();
        UserDto updatedDto = UserDto.builder()
                .id(1)
                .name("Tom")
                .email("tom@gmail.com")
                .age(20)
                .createdAt(time)
                .build();

        when(userService.updateUser(eq(1), any(UserRequestDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Tom"))
                .andExpect(jsonPath("$.email").value("tom@gmail.com"))
                .andExpect(jsonPath("$.age").value("20"))
                .andExpect(jsonPath("$.createdAt").value(time));
    }

    @Test
    void updateUserNotFound() throws Exception{
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("Max")
                .email("max@gmail.com")
                .age(25)
                .build();

        when(userService.updateUser(eq(200), any(UserRequestDto.class)))
                .thenThrow(new RuntimeException("ID не найден"));

        mockMvc.perform(put("/users/200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserSuccess() throws Exception{
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1);
    }
}
