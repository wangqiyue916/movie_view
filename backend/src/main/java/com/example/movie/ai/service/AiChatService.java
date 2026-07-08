package com.example.movie.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.ai.entity.AiChatMessage;
import com.example.movie.ai.entity.AiChatSession;
import com.example.movie.ai.mapper.AiChatMessageMapper;
import com.example.movie.ai.mapper.AiChatSessionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiChatService {

    private final AiChatSessionMapper aiChatSessionMapper;
    private final AiChatMessageMapper aiChatMessageMapper;

    public AiChatService(AiChatSessionMapper aiChatSessionMapper, AiChatMessageMapper aiChatMessageMapper) {
        this.aiChatSessionMapper = aiChatSessionMapper;
        this.aiChatMessageMapper = aiChatMessageMapper;
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

        // 2. Generate AI response based on keyword matching
        String response;
        String relatedType = null;
        Long relatedId = null;

        String msg = message != null ? message.toLowerCase() : "";

        if (containsAny(msg, "星际穿越")) {
            response = "《星际穿越》（Interstellar）是克里斯托弗·诺兰执导的科幻电影，讲述了一组宇航员穿越虫洞寻找人类新家园的故事。电影涉及黑洞、引力时间膨胀、五维空间等科学概念，由马修·麦康纳和安妮·海瑟薇主演，汉斯·季默配乐。推荐您观看相关解读视频了解更多细节！";
            relatedType = "MOVIE";
            relatedId = 1L;
        } else if (containsAny(msg, "盗梦空间")) {
            response = "《盗梦空间》（Inception）是诺兰导演的经典之作，讲述了一群"盗梦者"通过共享梦境潜入他人潜意识的故事。电影以其多层嵌套的梦境结构和开放式结局著称，由莱昂纳多·迪卡普里奥主演。推荐您观看结局解析视频！";
            relatedType = "MOVIE";
            relatedId = 2L;
        } else if (containsAny(msg, "诺兰", "nolan")) {
            response = "克里斯托弗·诺兰（Christopher Nolan）是当代最著名的电影导演之一，代表作品包括《盗梦空间》、《星际穿越》、《蝙蝠侠：黑暗骑士》三部曲、《记忆碎片》、《敦刻尔克》、《奥本海默》等。他以复杂的非线性叙事、震撼的视觉奇观和深刻的哲学主题著称。以下为您推荐相关解读视频：\n\n1. 星际穿越深度解读：时间与爱的终极命题（Bilibili，热度1500）\n2. 诺兰电影宇宙：从盗梦空间到星际穿越（Bilibili，热度980）\n3. 盗梦空间结局解析：陀螺到底停没停（YouTube，热度2000）";
            relatedType = "VIDEO";
            relatedId = 1L;
        } else if (containsAny(msg, "演员", "主演")) {
            response = "为您推荐以下热门演员信息：\n\n1. 马修·麦康纳 - 《星际穿越》主演，凭《达拉斯买家俱乐部》获奥斯卡影帝\n2. 莱昂纳多·迪卡普里奥 - 《盗梦空间》主演，凭《荒野猎人》获奥斯卡影帝\n3. 安妮·海瑟薇 - 《星际穿越》女主演，代表作《悲惨世界》\n\n您想了解哪位演员的更多作品？";
            relatedType = "MOVIE";
            relatedId = 1L;
        } else if (containsAny(msg, "导演")) {
            response = "为您推荐以下知名导演：\n\n1. 克里斯托弗·诺兰 - 《星际穿越》《盗梦空间》《黑暗骑士》\n2. 斯蒂文·斯皮尔伯格 - 《辛德勒的名单》《拯救大兵瑞恩》\n3. 詹姆斯·卡梅隆 - 《泰坦尼克号》《阿凡达》\n\n您想了解哪位导演的更多作品？";
            relatedType = "MOVIE";
            relatedId = 2L;
        } else if (containsAny(msg, "周边", "商品")) {
            response = "为您推荐热门电影周边商品：\n\n1. 星际穿越主题海报 - ¥39.90（淘宝）\n2. 盗梦空间陀螺模型 - ¥89.00（京东）\n3. 星际穿越TARS机器人模型 - ¥299.00（淘宝）\n4. 流浪地球2金属书签套装 - ¥29.90（拼多多）\n5. 暗夜骑士蝙蝠面具收藏版 - ¥199.00（京东）\n6. 盗梦空间主题笔记本 - ¥49.90（淘宝）";
            relatedType = "MERCHANDISE";
            relatedId = 1L;
        } else if (containsAny(msg, "资讯", "新闻")) {
            response = "为您推荐最新电影资讯：\n\n1. 暑期档科幻电影热度持续升温 - 多部科幻题材影片带动观影讨论\n2. 经典高分电影长评征集活动开启 - 平台鼓励更深入的电影讨论\n3. 本周口碑片单带动二刷热度 - 高分影片长线表现回暖\n\n访问资讯页面获取更多信息！";
            relatedType = "NEWS";
            relatedId = 1L;
        } else if (containsAny(msg, "视频", "解读")) {
            response = "为您推荐热门电影解读视频：\n\n1. 星际穿越深度解读：时间与爱的终极命题（Bilibili，热度1500）\n2. 盗梦空间结局解析：陀螺到底停没停（YouTube，热度2000）\n3. 《星际穿越》科学顾问谈黑洞与五维空间（YouTube，热度1200）\n4. 诺兰电影宇宙：从盗梦空间到星际穿越（Bilibili，热度980）\n5. 盗梦空间的多层梦境结构全解析（Bilibili，热度1600）";
            relatedType = "VIDEO";
            relatedId = 1L;
        } else {
            response = "您好！我是您的电影助手，可以帮您：\n\n🎬 推荐好看的电影和热门解读\n🎭 查询演员和导演信息\n🛍️ 发现电影周边商品\n📰 了解最新电影资讯\n🎥 观看电影解读视频\n\n您可以直接问我关于《星际穿越》、《盗梦空间》、诺兰导演等话题！";
            relatedType = null;
            relatedId = null;
        }

        // 3. Save AI response
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

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
