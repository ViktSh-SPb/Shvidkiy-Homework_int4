package org.example.shvidkiyhomework_int4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
import org.example.shvidkiyhomework_int4.entity.UserEntity;
import org.example.shvidkiyhomework_int4.exception.UserNotFoundException;
import org.example.shvidkiyhomework_int4.repository.UserRepository;
import org.example.shvidkiyhomework_int4.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Viktor Shvidkiy
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class UserControllerIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("test_db")
            .withUsername("user")
            .withPassword("user");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", ()->"create-drop");
    }

    @Test
    void createUserSuccess() throws Exception{
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("Jack")
                .email("jack@gmail.com")
                .age(20)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Jack"))
                .andExpect(jsonPath("$.email").value("jack@gmail.com"))
                .andExpect(jsonPath("$.age").value("20"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void getAllUsersShouldReturnList() throws Exception {
        userRepository.save(UserEntity.builder()
                .name("Jack")
                .email("jack@gmail.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build());
        userRepository.save(UserEntity.builder()
                .name("Bill")
                .email("bill@gmail.com")
                .age(35)
                .createdAt(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Jack"))
                .andExpect(jsonPath("$[0].email").value("jack@gmail.com"))
                .andExpect(jsonPath("$[0].age").value("30"))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[1].name").value("Bill"))
                .andExpect(jsonPath("$[1].email").value("bill@gmail.com"))
                .andExpect(jsonPath("$[1].age").value("35"))
                .andExpect(jsonPath("$[1].createdAt").isNotEmpty());
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

        when(userService.getUserById(1)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@gmail.com"))
                .andExpect(jsonPath("$.age").value("40"))
                .andExpect(jsonPath("$.createdAt").value(time));
    }

    @Test
    void getUserByIdNotFound() throws Exception{
        int userId = 100;
        when(userService.getUserById(userId))
                .thenThrow(new UserNotFoundException("Пользователь с ID: " + userId + " не найден."));

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь с ID: 100 не найден."));
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
