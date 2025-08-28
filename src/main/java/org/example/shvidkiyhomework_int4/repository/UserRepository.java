package org.example.shvidkiyhomework_int4.repository;

import org.example.shvidkiyhomework_int4.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
