package com.example.GeminiTest.Security.Repositories;

import com.example.GeminiTest.Security.UserPackage.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
    void deleteByUsername(String username);
    String getPasswordByUsername(String username);
}
