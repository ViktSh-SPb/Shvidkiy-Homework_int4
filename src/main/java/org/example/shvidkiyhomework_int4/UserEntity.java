package org.example.shvidkiyhomework_int4;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author Viktor Shvidkiy
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private Integer age;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
