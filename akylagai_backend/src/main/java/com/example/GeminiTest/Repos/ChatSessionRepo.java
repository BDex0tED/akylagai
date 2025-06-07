package com.example.GeminiTest.Repos;

import com.example.GeminiTest.Models.ChatSession;
import com.example.GeminiTest.Security.UserPackage.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepo extends CrudRepository<ChatSession, Long> {
    Optional<ChatSession> findByUserUsernameAndId(String username, Long id);
    List<ChatSession> findAllByUser(UserEntity user);

}
