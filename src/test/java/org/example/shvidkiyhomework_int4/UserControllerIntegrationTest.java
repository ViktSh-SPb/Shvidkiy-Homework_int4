package org.example.shvidkiyhomework_int4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.shvidkiyhomework_int4.dto.UserDto;
import org.example.shvidkiyhomework_int4.dto.UserRequestDto;
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
        UserDto user1 = userService.createUser(UserRequestDto.builder()
                        .name("Jack")
                        .email("jack@gmail.com")
                        .age(30)
                .build());
        UserDto user2 = userService.createUser(UserRequestDto.builder()
                .name("Bill")
                .email("bill@gmail.com")
                .age(35)
                .build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[0].age").value(user1.getAge()))
                .andExpect(jsonPath("$[0].createdAt").value(user1.getCreatedAt()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()))
                .andExpect(jsonPath("$[1].age").value(user2.getAge()))
                .andExpect(jsonPath("$[1].createdAt").value(user2.getCreatedAt()));
    }

    @Test
    void getUserByIdFound() throws Exception {
        UserDto user = userService.createUser(UserRequestDto.builder()
                .name("Jack")
                .email("jack@gmail.com")
                .age(30)
                .build());

        mockMvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.age").value(user.getAge()))
                .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt()));
    }

    @Test
    void getUserByIdNotFound() throws Exception{
        int userId = 100;

        mockMvc.perform(get("/users/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь с ID: 100 не найден."));
    }

    @Test
    void updateUserSuccess() throws Exception{
        UserDto user = userService.createUser(UserRequestDto.builder()
                .name("Jack")
                .email("jack@gmail.com")
                .age(30)
                .build());

        UserRequestDto updatedUser = UserRequestDto.builder()
                .name("MrJack")
                .email("ceo_jack@gmail.com")
                .age(31)
                .build();

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.age").value(updatedUser.getAge()))
                .andExpect(jsonPath("$.createdAt").value(user.getCreatedAt()));
    }

    @Test
    void updateUserNotFound() throws Exception{
        UserRequestDto requestDto = UserRequestDto.builder()
                .name("Max")
                .email("max@gmail.com")
                .age(25)
                .build();

        mockMvc.perform(put("/users/200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь с ID: 200 не найден."));
    }

    @Test
    void deleteUserSuccess() throws Exception{
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
