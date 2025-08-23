package org.example.shvidkiyhomework_int4;

import lombok.Builder;
import lombok.Data;

/**
 * @author Viktor Shvidkiy
 */
@Data
@Builder
public class UserRequestDto {
    private String name;
    private String email;
    private Integer age;
}
