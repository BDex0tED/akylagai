package com.example.GeminiTest.Services;

import com.example.GeminiTest.DTO.*;
import com.example.GeminiTest.DTO.CheckingTestDTO.CheckTestDTO;
import com.example.GeminiTest.DTO.CheckingTestDTO.QuestionCheckTestDTO;
import com.example.GeminiTest.Models.ChatSession;
import com.example.GeminiTest.Models.Message;
import com.example.GeminiTest.Repos.ChatSessionRepo;
import com.example.GeminiTest.Repos.MessageRepo;
import com.example.GeminiTest.Security.Repositories.UserRepository;
import com.example.GeminiTest.Security.UserPackage.UserEntity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeminiService {
    private final ChatClient.Builder chatClientBuilder;
    private final ChatSessionRepo chatSessionRepository;
    private final MessageRepo messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public GeminiService(ChatClient.Builder chatClientBuilder,
                         ChatSessionRepo chatSessionRepository,
                         MessageRepo messageRepository, UserRepository userRepository) {
        this.chatClientBuilder = chatClientBuilder;
        this.chatSessionRepository = chatSessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public Long createSession(String title) {
        ChatSession session = new ChatSession();
        session.setTitle(title);
        chatSessionRepository.save(session);
        return session.getId();
    }

    public String getAnswer(Principal principal,Long sessionId, PromptDTO promptDTO) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        session.setUser(user);

        ChatClient client = chatClientBuilder.build();

        Message userMessage = new Message();
        userMessage.setChatSession(session);
        userMessage.setRole("user");

        String promptString = """
        Определи школьный предмет вопроса, затем ответь на него.
        Поддерживаемые предметы:кыргыз тил, кыргыз адабияты, математика, физика, химия, литература, история, кыргыз тарыхы, биология, география, обществознание.
        Язык ответа: %s
        Вопрос: %s
        Стиль ответа: %s
        Ответь следуя следующему режиму: %s
        """.formatted(promptDTO.getLanguage(), promptDTO.getPrompt(),
                promptDTO.getTypeOfResponse(), promptDTO.getResponseMode());
        String userPrompt = """
                %s
                %s
                %s
                %s
                """.formatted(promptDTO.getPrompt(),promptDTO.getLanguage(),promptDTO.getTypeOfResponse(),promptDTO.getResponseMode());
        userMessage.setContent(userPrompt);
        messageRepository.save(userMessage);

        String response = client.prompt(promptString).call().content();

        Message assistantMessage = new Message();
        assistantMessage.setChatSession(session);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        messageRepository.save(assistantMessage);

        return response;
    }
    public String checkUserAnswer(Principal principal,Long sessionId, CheckDTO checkDTO) {
        ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        session.setUser(user);

        Message userMessage = new Message();
        userMessage.setChatSession(session);
        userMessage.setRole("user");

        ChatClient client = chatClientBuilder.build();
        String promptString = """
        Определи школьный предмет вопроса, затем проверь мой ответ.
        Вопрос: %s
        Ответ: %s
        Язык ответа: %s
""".formatted(checkDTO.getUserQuestion(), checkDTO.getUserAnswer(), checkDTO.getLanguage());

        String userPrompt = """
                %s
                %s
                %s
                """.formatted(checkDTO.getUserQuestion(),checkDTO.getUserAnswer(),checkDTO.getLanguage());

        userMessage.setContent(userPrompt);
        messageRepository.save(userMessage);

        String response = client.prompt(promptString).call().content();
        Message assistantMessage = new Message();
        assistantMessage.setChatSession(session);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        messageRepository.save(assistantMessage);

        return response;
    }
    public String testUserByQuestions(Principal principal, Long sessionId, TestUserDTO testUserDTO){
        ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        session.setUser(user);

        Message userMessage = new Message();
        userMessage.setChatSession(session);
        userMessage.setRole("user");
        messageRepository.save(userMessage);

        ChatClient client = chatClientBuilder.build();
        String promptString = """
                Определи школьный предмет вопроса: %s, затем составь %s вопросов по этой теме
                Язык ответа: %s
                Сложность вопросов: %s
                """.formatted(testUserDTO.getQuestionsTopic(), testUserDTO.getQuestionsNum(),
                testUserDTO.getLanguage(), testUserDTO.getDifficulty());

        String userPrompt = """
                %s
                %s
                %s
                %s
                """.formatted(testUserDTO.getQuestionsNum(),testUserDTO.getDifficulty(),
                testUserDTO.getQuestionsTopic(),testUserDTO.getLanguage()).formatted(testUserDTO.getQuestionsNum(),
                testUserDTO.getDifficulty(),testUserDTO.getQuestionsTopic(), testUserDTO.getLanguage());

        userMessage.setContent(userPrompt);
        messageRepository.save(userMessage);

        String response = client.prompt(promptString).call().content();

        Message assistantMessage = new Message();
        assistantMessage.setChatSession(session);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        messageRepository.save(assistantMessage);

        return response;
    }
    public String preparationPlan(Principal principal, Long SessionId, PreparationPlanDTO preparationPlanDTO) {
        ChatSession session = chatSessionRepository.findById(SessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        session.setUser(user);

        Message userMessage = new Message();
        userMessage.setChatSession(session);
        userMessage.setRole("user");

        ChatClient client = chatClientBuilder.build();
        String promptString = """
                Определи школьный предмет вопроса: %s, а затем составь план подготовки
                чтобы прям хорошо знать эту тему.
                Дополнительные пожелания: %s
                Язык ответа: %s
               """.formatted(preparationPlanDTO.getTopic(), preparationPlanDTO.getPrompt(), preparationPlanDTO.getLanguage());

        String userPrompt = """
                %s
                %s
                """.formatted(preparationPlanDTO.getTopic(), preparationPlanDTO.getPrompt());
        userMessage.setContent(userPrompt);
        messageRepository.save(userMessage);


        String response = client.prompt(promptString).call().content();
        Message assistantMessage = new Message();
        assistantMessage.setChatSession(session);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        messageRepository.save(assistantMessage);

        return response;
    }
    public List<MessageDTO> getHistory(Long sessionId, Principal principal) throws AccessDeniedException {
        ChatSession session = chatSessionRepository
                .findByUserUsernameAndId(principal.getName(), sessionId)
                .orElseThrow(() -> new AccessDeniedException("You don't have access to this session"));

        List<Message> messages = messageRepository.findByChatSessionIdOrderByTimestampAsc(session.getId());

        return messages.stream()
                .map(m -> new MessageDTO(m.getChatSession().getId(), m.getRole(), m.getContent()))
                .collect(Collectors.toList());
    }
//    public String checkTest(Principal principal, CheckTestDTO dto) {
//        ChatSession session = chatSessionRepository.findById(dto.getSessionId())
//                .orElseThrow(() -> new RuntimeException("Session not found"));
//        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
//        session.setUser(user);
//
//        StringBuilder promptBuilder = new StringBuilder("Проверь ответы пользователя на следующие вопросы:\n");
//
//        for (QuestionCheckTestDTO qa : dto.getTestList()) {
//            promptBuilder.append("Вопрос: ").append(qa.getQuestion()).append("\n");
//            promptBuilder.append("Ответ пользователя: ").append(qa.getAnswer()).append("\n\n");
//        }
//
//        promptBuilder.append("Дай корректность каждого ответа и пояснение, если нужно.");
//
//        Message userMessage = new Message();
//        userMessage.setChatSession(session);
//        userMessage.setRole("user");
//        userMessage.setContent(promptBuilder.toString());
//        messageRepository.save(userMessage);
//
//        ChatClient client = chatClientBuilder.build();
//        String response = client.prompt(promptBuilder.toString()).call().content();
//
//        Message assistantMessage = new Message();
//        assistantMessage.setChatSession(session);
//        assistantMessage.setRole("assistant");
//        assistantMessage.setContent(response);
//        messageRepository.save(assistantMessage);
//
//        return response;
//    }
    public String makeSimplier(long sessionId, Principal principal) throws AccessDeniedException {
        List<MessageDTO> userPrompt = getHistory(sessionId,principal);
        if (userPrompt.isEmpty()) {
            throw new IllegalStateException("История пуста, нет сообщений для обработки.");
        }
        MessageDTO lastMessage = userPrompt.get(userPrompt.size() - 1);
        if (!"assistant".equalsIgnoreCase(lastMessage.getRole())) {
            throw new IllegalStateException("Последнее сообщение не от ассистента.");
        }
        String prompt = lastMessage.getContent();

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        session.setUser(user);

        ChatClient client = chatClientBuilder.build();

        Message userMessage = new Message();
        userMessage.setChatSession(session);
        userMessage.setRole("user");
        userMessage.setContent(prompt);
        messageRepository.save(userMessage);

        String geminiPrompt = """
                %s, объясни проще. Без лишних слов""".formatted(prompt);
        String response = client.prompt(geminiPrompt).call().content();
        Message assistantMessage = new Message();
        assistantMessage.setChatSession(session);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        messageRepository.save(assistantMessage);
        return response;
    }
    public List<ChatSessionDTO> getUserSessions(Principal principal) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ChatSession> sessions = chatSessionRepository.findAllByUser(user);

        return sessions.stream()
                .map(session -> new ChatSessionDTO(session.getId(), session.getTitle()))
                .toList();
    }

}


//@Service
//public class GeminiService {
//    private final ChatClient.Builder chatClientBuilder;
//
//    @Autowired
//    public GeminiService(ChatClient.Builder chatClientBuilder) {
//        this.chatClientBuilder = chatClientBuilder;
//    }
//
//    public String getAnswer(PromptDTO promptDTO) {
//        ChatClient client = chatClientBuilder.build();
//        String promptString = """
//    Определи школьный предмет вопроса, затем ответь на него.
//    Поддерживаемые предметы:кыргыз тил, кыргыз адабияты, математика, физика, химия, литература, история, кыргыз тарыхы, биология, география, обществознание.
//    Язык ответа: %s
//    Вопрос: %s
//    Стиль ответа: %s
//    Ответь следуя следующему режиму: %s
//""".formatted(promptDTO.getLanguage(), promptDTO.getPrompt(), promptDTO.getTypeOfResponse(), promptDTO.getResponseMode());
//        String response = client.prompt(promptString).call().content();
//        return response;
//    }
//    public String checkUserAnswer(CheckDTO checkDTO) {
//        ChatClient client = chatClientBuilder.build();
//        String promptString = """
//        Определи школьный предмет вопроса, затем проверь мой ответ.
//        Вопрос:%s
//        Ответ:%s
//        Язык ответа: %s
//""".formatted(checkDTO.getUserAnswer(), checkDTO.getUserQuestion(), checkDTO.getLanguage());
//        String response = client.prompt(promptString).call().content();
//        return response;
//    }
//    public String testUserByQuestions(TestUserDTO testUserDTO) {
//        ChatClient client = chatClientBuilder.build();
//        String promptString = """
//                Определи школьный предмет вопроса: %s, затем составь %s вопросов по этой теме
//                Язык ответа: %s
//                Сложность вопросов: %s
//                """.formatted(testUserDTO.getQuestionsTopic(), testUserDTO.getQuestionsNum(),
//                testUserDTO.getLanguage(), testUserDTO.getDifficulty());
//        String response = client.prompt(promptString).call().content();
//        return response;
//    }
//    public String preparationPlan(PreparationPlanDTO preparationPlanDTO) {
//        ChatClient client = chatClientBuilder.build();
//        String promptString = """
//                Определи школьный предмет вопроса: %s, а затем составь план подготовки
//                чтобы прям хорошо знать эту тему.
//                Дополнительные пожелания:%s
//               """.formatted(preparationPlanDTO.getTopic(), preparationPlanDTO.getPrompt());
//        String response = client.prompt(promptString).call().content();
//        return response;
//    }
//}
