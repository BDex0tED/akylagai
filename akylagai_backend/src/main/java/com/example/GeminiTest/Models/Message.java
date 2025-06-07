package com.example.GeminiTest.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatSession chatSession;

    private String role;
    @Column(length = 6000)
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();

}