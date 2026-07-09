package com.example.movie.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.ai.entity.AiChatMessage;
import com.example.movie.ai.entity.AiChatSession;
import com.example.movie.ai.mapper.AiChatMessageMapper;
import com.example.movie.ai.mapper.AiChatSessionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AiChatService {

    private final AiChatSessionMapper aiChatSessionMapper;
    private final AiChatMessageMapper aiChatMessageMapper;
    private final LlmService llmService;

    public AiChatService(AiChatSessionMapper aiChatSessionMapper,
                         AiChatMessageMapper aiChatMessageMapper,
                         LlmService llmService) {
        this.aiChatSessionMapper = aiChatSessionMapper;
        this.aiChatMessageMapper = aiChatMessageMapper;
        this.llmService = llmService;
    }

    public AiChatSession createSession(Long userId) {
        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        session.setTitle("新对话");
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        aiChatSessionMapper.insert(session);
        return session;
    }

    public AiChatSession getSession(Long sessionId) {
        return aiChatSessionMapper.selectById(sessionId);
    }

    public List<AiChatMessage> getMessages(Long sessionId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId)
                .orderByAsc(AiChatMessage::getCreatedAt);
        return aiChatMessageMapper.selectList(wrapper);
    }

    public AiChatMessage chat(Long sessionId, String message) {
        // 1. Save user message
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setRole("USER");
        userMsg.setContent(message);
        userMsg.setCreatedAt(LocalDateTime.now());
        aiChatMessageMapper.insert(userMsg);

        // 2. 优先调用 DeepSeek 大模型
        String response;
        String relatedType = null;
        Long relatedId = null;

        String llmResponse = tryLlmChat(message, sessionId);
        if (llmResponse != null) {
            response = llmResponse;
        } else {
            // LLM 不可用时，使用关键词匹配兜底
            String fallback = generateKeywordResponse(message);
            response = fallback;
            // 尝试从关键词中提取关联信息
            String msg = message != null ? message.toLowerCase() : "";
            if (msg.contains("星际穿越")) { relatedType = "MOVIE"; relatedId = 1L; }
            else if (msg.contains("盗梦空间")) { relatedType = "MOVIE"; relatedId = 2L; }
            else if (msg.contains("流浪地球")) { relatedType = "MOVIE"; relatedId = 3L; }
            else if (msg.contains("周边") || msg.contains("商品") || msg.contains("鼠标垫")) { relatedType = "MERCHANDISE"; relatedId = 1L; }
            else if (msg.contains("视频") || msg.contains("解读")) { relatedType = "VIDEO"; relatedId = 1L; }
            else if (msg.contains("资讯") || msg.contains("新闻")) { relatedType = "NEWS"; relatedId = 1L; }
            else { relatedType = "MOVIE"; relatedId = 1L; }
        }

        // 3. 保存 AI 回复
        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setRole("ASSISTANT");
        aiMsg.setContent(response);
        aiMsg.setRelatedType(relatedType);
        aiMsg.setRelatedId(relatedId);
        aiMsg.setCreatedAt(LocalDateTime.now());
        aiChatMessageMapper.insert(aiMsg);

        // Update session title from first user message if still default
        AiChatSession session = aiChatSessionMapper.selectById(sessionId);
        if (session != null && "新对话".equals(session.getTitle())) {
            String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
            session.setTitle(title);
            session.setUpdatedAt(LocalDateTime.now());
            aiChatSessionMapper.updateById(session);
        }

        return aiMsg;
    }

    public void deleteSession(Long sessionId) {
        aiChatSessionMapper.deleteById(sessionId);
    }

    /**
     * 尝试调用 DeepSeek 大模型
     * @return AI 回复，失败返回 null
     */
    private String tryLlmChat(String message, Long sessionId) {
        // 构建历史消息
        List<Map<String, String>> history = new ArrayList<>();
        List<AiChatMessage> msgList = getMessages(sessionId);
        // 只取当前消息之前的历史（不含刚插入的用户消息）
        for (int i = 0; i < msgList.size() - 1; i++) {
            AiChatMessage m = msgList.get(i);
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("role", m.getRole().toLowerCase());
            entry.put("content", m.getContent());
            history.add(entry);
        }
        return llmService.chat(message, history);
    }

    /**
     * 关键词匹配兜底回复
     */
    private String generateKeywordResponse(String message) {
        if (message == null) message = "";
        String msg = message.toLowerCase();

        if (containsAny(msg, "星际穿越")) {
            return "《星际穿越》（Interstellar）是克里斯托弗·诺兰执导的科幻电影，讲述了一组宇航员穿越虫洞寻找人类新家园的故事。电影涉及黑洞、引力时间膨胀、五维空间等科学概念，由马修·麦康纳和安妮·海瑟薇主演，汉斯·季默配乐。";
        } else if (containsAny(msg, "盗梦空间")) {
            return "《盗梦空间》（Inception）是诺兰导演的经典之作，讲述了一群「盗梦者」通过共享梦境潜入他人潜意识的故事。电影以其多层嵌套的梦境结构和开放式结局著称，由莱昂纳多·迪卡普里奥主演。";
        } else if (containsAny(msg, "诺兰", "nolan")) {
            return "克里斯托弗·诺兰代表作品包括《盗梦空间》《星际穿越》《黑暗骑士》三部曲、《记忆碎片》《敦刻尔克》《奥本海默》等。他以复杂的非线性叙事和震撼的视觉奇观著称。";
        } else if (containsAny(msg, "演员", "主演")) {
            return "热门演员：马修·麦康纳、莱昂纳多·迪卡普里奥、安妮·海瑟薇等。您想了解哪位演员？";
        } else if (containsAny(msg, "导演")) {
            return "知名导演：克里斯托弗·诺兰、斯蒂文·斯皮尔伯格、詹姆斯·卡梅隆等。您想了解哪位导演？";
        } else if (containsAny(msg, "周边", "商品")) {
            return "为您推荐热门电影周边：星际穿越主题海报 ¥39.90、盗梦空间陀螺模型 ¥89.00、TARS机器人模型 ¥299.00 等。";
        } else if (containsAny(msg, "资讯", "新闻")) {
            return "最新电影资讯：暑期档科幻电影热度持续升温，经典高分电影长评征集活动开启中。";
        } else if (containsAny(msg, "视频", "解读")) {
            return "热门解读视频：星际穿越深度解读（Bilibili）、盗梦空间结局解析（YouTube）、诺兰电影宇宙（Bilibili）。";
        } else {
            return "您好！我是电影助手，可以帮您推荐电影、查询演员导演、发现周边商品、了解最新资讯、观看解读视频。请问有什么可以帮您的？";
        }
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
