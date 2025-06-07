package com.example.GeminiTest.Repos;

import com.example.GeminiTest.Models.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Integer> {
    List<Message> findByChatSessionIdOrderByTimestampAsc(Long sessionId);
}
