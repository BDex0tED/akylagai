package com.example.GeminiTest.Controllers;

import com.example.GeminiTest.DTO.*;
import com.example.GeminiTest.DTO.CheckingTestDTO.CheckTestDTO;
import com.example.GeminiTest.Services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/startSession")
    public ResponseEntity<Long> startSession(@RequestBody SessionTitleDTO sessionTitleDTO, Principal principal) {
        Long sessionId = geminiService.createSession(sessionTitleDTO.getTitle());
        return ResponseEntity.ok(sessionId);
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askGemini(@RequestBody PromptDTO promptDTO, Principal principal) {
        Long sessionId = (promptDTO.getSessionId() != null)
                ? promptDTO.getSessionId()
                : geminiService.createSession(promptDTO.getPrompt());

        String response = geminiService.getAnswer(principal, sessionId, promptDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkAnswer")
    public ResponseEntity<String> checkAnswer(@RequestBody CheckDTO checkDTO, Principal principal) {
        Long sessionId = (checkDTO.getSessionId() != null)
                ? checkDTO.getSessionId()
                : geminiService.createSession(checkDTO.getUserQuestion());

        String response = geminiService.checkUserAnswer(principal, sessionId, checkDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/testUser")
    public ResponseEntity<String> testUser(@RequestBody TestUserDTO testUserDTO, Principal principal) {
        Long sessionId = (testUserDTO.getSessionId() != null)
                ? testUserDTO.getSessionId()
                : geminiService.createSession(testUserDTO.getQuestionsTopic());

        String response = geminiService.testUserByQuestions(principal, sessionId, testUserDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/preparationPlan")
    public ResponseEntity<String> preparationPlan(@RequestBody PreparationPlanDTO preparationPlanDTO, Principal principal) {
        Long sessionId = (preparationPlanDTO.getSessionId() != null)
                ? preparationPlanDTO.getSessionId()
                : geminiService.createSession(preparationPlanDTO.getPrompt());

        String response = geminiService.preparationPlan(principal, sessionId, preparationPlanDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<MessageDTO>> getMessageHistory(@PathVariable Long sessionId, Principal principal) throws AccessDeniedException {
        List<MessageDTO> history = geminiService.getHistory(sessionId, principal);
        return ResponseEntity.ok(history);
    }
    @PostMapping("/makeSimplier")
    public ResponseEntity<String> makeSimplier(@RequestParam("sessionId") long sessionId, Principal principal) throws AccessDeniedException {
        String result = geminiService.makeSimplier(sessionId, principal);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionDTO>> getUserSessions(Principal principal) {
        List<ChatSessionDTO> sessions = geminiService.getUserSessions(principal);
        return ResponseEntity.ok(sessions);
    }


}
