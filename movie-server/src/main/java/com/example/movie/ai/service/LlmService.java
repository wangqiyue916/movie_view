package com.example.movie.ai.service;

import com.example.movie.ai.config.LlmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    private final RestTemplate restTemplate;
    private final LlmConfig llmConfig;

    private static final String SYSTEM_PROMPT = """
            你是"电影助手"，一个专注于电影领域的智能AI助手。你的职责包括：
            
            1. 电影推荐：根据用户喜好推荐电影，说明推荐理由
            2. 演员/导演信息：介绍演员和导演的代表作品、风格特点
            3. 电影周边：推荐电影相关的周边商品
            4. 电影资讯：提供最新电影新闻和动态
            5. 电影解读：介绍知名电影的深度解读和幕后故事
            
            回答要求：
            - 使用中文回复，语言亲切自然
            - 回答简洁有料，不需要长篇大论
            - 当推荐具体电影时，提及导演和主演
            - 如果用户询问不相关的话题，礼貌地引导回电影话题
            - 可以适度推荐用户可能感兴趣的电影周边商品
            """;

    public LlmService(RestTemplate restTemplate, LlmConfig llmConfig) {
        this.restTemplate = restTemplate;
        this.llmConfig = llmConfig;
    }

    public String chat(String userMessage, List<Map<String, String>> history) {
        if (!llmConfig.isConfigured()) {
            log.warn("DeepSeek API Key 未配置，返回 null");
            return null;
        }

        try {
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示
            Map<String, String> systemMsg = new LinkedHashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);
            
            // 历史消息（最多保留最近10轮）
            if (history != null && !history.isEmpty()) {
                int start = Math.max(0, history.size() - 20);
                for (int i = start; i < history.size(); i++) {
                    messages.add(history.get(i));
                }
            }
            
            // 当前用户消息
            Map<String, String> userMsg = new LinkedHashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            // 构建请求体
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", llmConfig.getModel());
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);
            requestBody.put("stream", false);

            // 构建请求头
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + llmConfig.getApiKey());
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");

            org.springframework.http.HttpEntity<Map<String, Object>> entity =
                    new org.springframework.http.HttpEntity<>(requestBody, headers);

            log.info("调用 DeepSeek API: model={}, userMessage={}", llmConfig.getModel(),
                    userMessage.length() > 50 ? userMessage.substring(0, 50) + "..." : userMessage);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    llmConfig.getApiUrl(), entity, Map.class);

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        String content = (String) message.get("content");
                        log.info("DeepSeek 返回响应成功，长度: {}", content != null ? content.length() : 0);
                        return content;
                    }
                }
            }

            log.warn("DeepSeek 响应格式异常: {}", response);
            return null;
        } catch (Exception e) {
            log.error("调用 DeepSeek API 失败: {}", e.getMessage());
            return null;
        }
    }
}
