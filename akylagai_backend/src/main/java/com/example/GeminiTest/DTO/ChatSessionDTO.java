package com.example.GeminiTest.DTO;

import lombok.Data;

@Data
public class ChatSessionDTO {
    private Long id;
    private String title;

    public ChatSessionDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }

}

