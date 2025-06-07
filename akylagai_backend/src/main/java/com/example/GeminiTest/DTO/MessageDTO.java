package com.example.GeminiTest.DTO;

import lombok.Data;

@Data
public class MessageDTO {
    private Long sessionId;
    private String role;
    private String content;

    public MessageDTO(Long sessionId, String role, String content) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
    }
}
