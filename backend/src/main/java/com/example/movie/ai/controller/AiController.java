package com.example.movie.ai.controller;

import com.example.movie.ai.entity.AiChatMessage;
import com.example.movie.ai.entity.AiChatSession;
import com.example.movie.ai.service.AiChatService;
import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/sessions")
    public ApiResponse<AiChatSession> createSession() {
        Long userId = LoginUserContext.getUserId();
        AiChatSession session = aiChatService.createSession(userId);
        return ApiResponse.success(session);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResponse<List<AiChatMessage>> getMessages(@PathVariable Long sessionId) {
        List<AiChatMessage> messages = aiChatService.getMessages(sessionId);
        return ApiResponse.success(messages);
    }

    @PostMapping("/sessions/{sessionId}/chat")
    public ApiResponse<AiChatMessage> chat(@PathVariable Long sessionId, @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return ApiResponse.error(400, "消息内容不能为空");
        }
        AiChatMessage response = aiChatService.chat(sessionId, content);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable Long sessionId) {
        aiChatService.deleteSession(sessionId);
        return ApiResponse.success();
    }
}
