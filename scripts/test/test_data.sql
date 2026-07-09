-- ============================================================
-- 长评模块测试数据脚本
-- 使用方法：在MySQL中执行此脚本（需要先执行schema.sql）
-- ============================================================

USE movie;

-- 1. 插入测试用户（如果不存在）
INSERT IGNORE INTO users (id, username, password_hash, nickname, status) VALUES
(1, 'test_user', 'test', '测试用户', 'NORMAL'),
(2, 'review_writer', 'test', '影评作者', 'NORMAL'),
(3, 'admin', 'test', '管理员', 'NORMAL');

-- 2. 插入测试电影（如果不存在）
INSERT IGNORE INTO movies (id, title, director, actors, synopsis, poster_url, status) VALUES
(1, '星际穿越', '克里斯托弗·诺兰', '马修·麦康纳, 安妮·海瑟薇', '一部关于太空探索和人类存亡的科幻巨作...', 'https://example.com/poster1.jpg', 'ONLINE'),
(2, '肖申克的救赎', '弗兰克·德拉邦特', '蒂姆·罗宾斯, 摩根·弗里曼', '一个关于希望与救赎的经典故事...', 'https://example.com/poster2.jpg', 'ONLINE'),
(3, '千与千寻', '宫崎骏', '柊瑠美, 入野自由', '少女千寻在神秘世界的冒险之旅...', 'https://example.com/poster3.jpg', 'ONLINE');

-- 3. 插入测试长评
INSERT INTO long_reviews (id, movie_id, user_id, title, content_md, cover_url, status, view_count, like_count, favorite_count, reply_count, is_featured) VALUES
(1, 1, 2, '《星际穿越》——时间与爱的维度',
'## 写在前面\n\n《星际穿越》是诺兰最具野心的作品之一。\n\n## 剧情分析\n\n影片讲述了地球面临生态危机，前NASA飞行员库珀踏上星际旅程的故事。\n\n### 时间的相对性\n\n影片中**时间膨胀**的概念令人深思。在米勒星球上的一小时，相当于地球上的七年——这不仅是一个物理概念，更是对人类情感的终极考验。\n\n> \"我们曾经仰望星空，思索我们在宇宙中的位置。而现在我们只是低头，担心我们在泥土中的位置。\"\n\n## 视听语言\n\n汉斯·季默的配乐堪称经典，管风琴的宏大音色与太空的浩瀚完美契合。\n\n![剧照](https://example.com/interstellar.jpg)\n\n## 总结\n\n这是一部超越科幻类型的作品，它探讨的核心是**爱与时间**这两个永恒的主题。',
'https://example.com/review_cover1.jpg', 'ONLINE', 1520, 89, 45, 12, 1),

(2, 2, 2, '希望是件好东西——《肖申克的救赎》影评',
'## 关于希望\n\n安迪说：\"希望是件好东西，也许是世间最好的东西，而好东西永远不会消逝。\"\n\n这部电影告诉我们：\n- 信念可以战胜一切\n- 知识就是力量\n- 真正的自由来自内心\n\n## 人物分析\n\n### 安迪·杜弗兰\n安迪用**二十年**的时间，靠一把小锤子挖通了通往自由的道路。这不仅仅是一个越狱故事，更是一个关于坚持的寓言。\n\n### 瑞德\n摩根·弗里曼饰演的瑞德是整个故事的叙述者和见证者，他的转变令人动容。',
'https://example.com/review_cover2.jpg', 'ONLINE', 2340, 120, 67, 23, 1),

(3, 1, 1, '星际穿越的物理科普',
'## 虫洞与黑洞\n\n影片中涉及的物理概念包括：\n\n1. **虫洞**：时空的捷径\n2. **黑洞**：连光都无法逃脱的天体\n3. **引力时间膨胀**：强引力场中时间流速变慢\n\n基普·索恩作为科学顾问，确保了影片中物理概念的准确性。',
null, 'ONLINE', 680, 35, 12, 5, 0);

-- 4. 插入长评图片
INSERT INTO review_images (review_id, image_url, sort_order) VALUES
(1, 'https://example.com/interstellar_shot1.jpg', 0),
(1, 'https://example.com/interstellar_shot2.jpg', 1),
(2, 'https://example.com/shawshank_shot1.jpg', 0);

-- 5. 插入测试回复
INSERT INTO review_replies (id, review_id, user_id, parent_id, content, status, like_count) VALUES
(1, 1, 1, NULL, '写得真好！诺兰的作品确实值得反复品味。', 'NORMAL', 15),
(2, 1, 3, NULL, '关于时间膨胀的部分解读很到位。', 'NORMAL', 8),
(3, 1, 2, 1, '谢谢！诺兰的电影每次看都有新的感悟。', 'NORMAL', 5),
(4, 2, 1, NULL, '肖申克是我每年都会重温的电影。', 'NORMAL', 20),
(5, 2, 3, NULL, '摩根·弗里曼的旁白太有魅力了。', 'NORMAL', 12);

-- 6. 插入测试点赞记录
INSERT INTO likes (user_id, target_type, target_id) VALUES
(1, 'LONG_REVIEW', 1),
(1, 'LONG_REVIEW', 2),
(3, 'LONG_REVIEW', 1),
(1, 'REVIEW_REPLY', 1),
(3, 'REVIEW_REPLY', 1);

-- 7. 插入测试收藏记录
INSERT INTO favorites (user_id, target_type, target_id) VALUES
(1, 'LONG_REVIEW', 1),
(1, 'LONG_REVIEW', 2);

-- 8. 插入待审核的长评（测试审核流程用）
INSERT INTO long_reviews (id, movie_id, user_id, title, content_md, cover_url, status, view_count, like_count, favorite_count, reply_count, is_featured) VALUES
(4, 3, 1, '《千与千寻》的隐喻解析（待审核）',
'## 关于成长\n\n千寻的故事是一个典型的成长叙事...\n\n### 名字的隐喻\n\n汤婆婆夺走名字的行为象征着**社会对个体身份的消解**。',
null, 'PENDING', 0, 0, 0, 0, 0);

-- 验证插入结果
SELECT '=== 长评列表 ===' AS info;
SELECT id, title, status, is_featured, view_count, like_count, reply_count FROM long_reviews;

SELECT '=== 回复列表 ===' AS info;
SELECT id, review_id, user_id, parent_id, LEFT(content, 30) AS content_preview, like_count FROM review_replies;

SELECT '=== 点赞记录 ===' AS info;
SELECT user_id, target_type, target_id FROM likes;

SELECT '=== 收藏记录 ===' AS info;
SELECT user_id, target_type, target_id FROM favorites;