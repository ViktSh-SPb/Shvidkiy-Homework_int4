package org.example.shvidkiyhomework_int4.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Viktor Shvidkiy
 */
@Data
@Builder
public class UserDto {
    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private String createdAt;
}
