-- ============================================================
-- Long Review Module Demo Data - 郭俊岑 答辩演示
-- ============================================================
USE movie;

-- Clean existing long reviews (keep users/movies)
DELETE FROM review_replies;
DELETE FROM likes WHERE target_type = 'LONG_REVIEW';
DELETE FROM favorites WHERE target_type = 'LONG_REVIEW';
DELETE FROM reports WHERE target_type = 'LONG_REVIEW';
DELETE FROM review_images;
DELETE FROM audit_records;
DELETE FROM long_reviews;

-- ============================================================
-- 6 Rich Long Reviews with Markdown
-- ============================================================
INSERT INTO long_reviews (id, movie_id, user_id, title, content_md, status, view_count, like_count, favorite_count, reply_count, is_featured, featured_at, created_at, updated_at)
VALUES
(1, 1, 1, '穿越星际之后，仍然回到人的情感',
'## 关于《星际穿越》

这部电影最动人的地方，是它把**宏大的宇宙尺度**和亲情联系在一起。

### 为什么值得反复观看

真正让人反复回看的，并不只是视觉奇观，还有：

1. **父女情感线** —— Cooper和Murphy之间的时间错位感
2. **科学真实性** —— 虫洞、黑洞、时间膨胀的物理理论背书
3. **配乐的力量** —— Hans Zimmer用管风琴营造的宏大氛围

> "We used to look up at the sky and wonder at our place in the stars. Now we just look down, and worry about our place in the dirt."

```python
# 时间膨胀计算示例
def time_dilation(gravity, velocity):
    return 1 / (1 - (2 * gravity / c**2) - (velocity**2 / c**2)) ** 0.5
```

### 总结评分

| 维度 | 评分 |
|------|------|
| 剧情 | 9.2 |
| 特效 | 9.6 |
| 演技 | 9.1 |
| 配乐 | 9.8 |

[查看电影详情](/movies/1)',
'ONLINE', 258, 42, 12, 8, 1, NOW(), '2026-07-01 10:00:00', NOW()),

(2, 3, 3, '梦境不是谜题，而是欲望的回声——重评《盗梦空间》',
'## 重看《盗梦空间》

真正让人反复回看的，并不只是**层层嵌套的梦境结构**，还有每一层梦境背后未被说破的执念。

### 三层解读

- **表层**：悬疑动作片 —— 一群人进入梦境执行任务
- **中层**：心理探讨 —— Cobb对妻子的愧疚和自我救赎
- **深层**：哲学思辨 —— 究竟什么是真实？如何确定自己不是在做梦？

### 那个陀螺

电影结尾的*陀螺旋转*是影史上最经典的开放式结局之一。它到底倒没倒？

1. 倒了 → 这是现实世界，Cobb回到了家
2. 没倒 → Cobb还在梦境中，或者他根本不在乎了

> "Dreams feel real while were in them. Its only when we wake up that we realize something was actually strange."

**你更倾向于哪种解读？欢迎在评论区讨论。**

![盗梦空间剧照](/merch-shubiao.png)',
'ONLINE', 186, 35, 8, 12, 1, NOW(), '2026-07-02 14:30:00', NOW()),

(3, 2, 2, '灾难片里的群像，为什么《流浪地球2》仍然能打动人',
'## 当宏大叙事遇见个体命运

《流浪地球2》真正要讨论的不是奇观本身，而是：

### 群像塑造的成功

| 角色 | 立场 | 抉择 |
|------|------|------|
| 刘培强 | 宇航员 | 家庭 vs 使命 |
| 图恒宇 | 科学家 | 伦理 vs 思念 |
| 周喆直 | 外交官 | 团结 vs 利益 |

### 三条叙事线交织

**太空电梯危机 → 月球危机 → 木星危机**

每一阶段都不是单纯的灾难场面堆砌，而是通过个体选择推动宏大叙事：

1. 数字生命计划的伦理争议
2. 人类面对存亡时的分裂与团结
3. "带着家园去流浪"的文化内核

> "本计划将持续一百代人。尽管，我们不知道4.2光年外的新太阳会带来什么样的家园，但从今天开始，人类的勇气和坚毅将永刻于星空之下。"

**这部电影让中国科幻真正站起来了。**',
'ONLINE', 142, 28, 5, 6, 1, NOW(), '2026-07-03 09:15:00', NOW()),

(4, 1, 2, '为什么诺兰的叙事结构总能让人上瘾',
'## 诺兰的叙事方法论

克里斯托弗·诺兰的电影有一个共同特点：**非线性叙事结构**。

### 代表作品分析

- **《记忆碎片》**（2000）：正叙+倒叙交替
- **《致命魔术》**（2006）：日记嵌套
- **《盗梦空间》**（2010）：梦境层级嵌套
- **《星际穿越》**（2014）：时间膨胀造成的双线叙事
- **《信条》**（2020）：正向+逆向时间流

### 为什么这种叙事有效？

> "观众喜欢被挑战，但不喜欢被抛弃。"

诺兰的高明之处在于：

1. **信息释放节奏精准** —— 每一幕都给观众"刚好够"的信息
2. **情感锚点稳固** —— 再复杂的结构下都有清晰的情感线索
3. **视觉语言支撑** —— 用画面帮助理解叙事层级

```
推荐观影顺序：
新手：星际穿越 → 盗梦空间 → 致命魔术
进阶：记忆碎片 → 信条 → 敦刻尔克

核心逻辑：先建立对诺兰风格的信任，再尝试高难度作品
```

**你最喜欢诺兰的哪部电影？**',
'ONLINE', 96, 20, 3, 4, 0, NULL, '2026-07-04 16:45:00', NOW()),

(5, 2, 1, '科幻电影中的中国哲学：从《流浪地球》系列看文化自信',
'## 中国式科幻的独特魅力

《流浪地球》系列区别于好莱坞科幻的核心是什么？

### 三组对比

| 特征 | 好莱坞科幻 | 《流浪地球》 |
|------|-----------|-------------|
| 解决方式 | 寻找新家园 | **带着地球走** |
| 叙事焦点 | 个人英雄 | **集体群像** |
| 时间尺度 | 一代人 | **百代人** |
| 价值核心 | 个体自由 | **家园与传承** |

### 文化逻辑

中国科幻的底层逻辑源于：

- **愚公移山精神**：“子子孙孙无穷匮也”
- **家国情怀**：对故土的眷恋不是弱点
- **人类命运共同体**：危机面前没有国家能独善其身

> "希望是像钻石一样珍贵的东西。无论最终结果将人类历史导向何处，我们决定选择希望。"

### 反思

当然，系列第二部在*叙事节奏*和*女性角色塑造*上仍有提升空间。但作为中国科幻的开辟者，它的意义远大于瑕疵。

**我们不需要另一个好莱坞，我们需要自己的声音。**',
'ONLINE', 78, 15, 2, 3, 0, NULL, '2026-07-05 11:20:00', NOW()),

(6, 3, 2, '从《盗梦空间》到《奥本海默》：诺兰的15年进化轨迹',
'## 一个导演的自我超越

从2010年的《盗梦空间》到2023年的《奥本海默》，诺兰完成了从"**商业类型片大师**"到"**严肃作者导演**"的转变。

### 转变体现在三个方面

#### 1. 题材选择

```
盗梦空间（2010）→ 科幻动作
星际穿越（2014）→ 硬科幻/亲情
敦刻尔克（2017）→ 战争/实验叙事
信条（2020）     → 谍战/时间哲学
奥本海默（2023）→ 传记/道德困境
```

从**奇观驱动**逐渐转向**人物驱动**。

#### 2. 情感深度

早期作品的情感线是叙事的*配件*，后期作品的情感线开始成为*叙事核心*：

- 《星际穿越》中Cooper看着23年视频的场面
- 《奥本海默》中听证会的心理煎熬

#### 3. 技术实验

| 电影 | 技术亮点 |
|------|----------|
| 盗梦空间 | 旋转走廊实拍 |
| 星际穿越 | IMAX 70mm |
| 信条 | 正向+逆向实拍 |
| 奥本海默 | 黑白IMAX胶片 |

**诺兰证明了：商业大片和作者表达从来不是对立的。**',
'ONLINE', 64, 12, 3, 2, 0, NULL, '2026-07-06 08:00:00', NOW());

-- ============================================================
-- 1 PENDING review (for admin audit demo)
-- ============================================================
INSERT INTO long_reviews (id, movie_id, user_id, title, content_md, status, view_count, like_count, favorite_count, reply_count, is_featured, created_at, updated_at)
VALUES
(7, 2, 3, '关于《流浪地球2》中MOSS角色的深度解读（待审核）',
'## MOSS：不是反派，而是逻辑的极致

很多人看完《流浪地球2》认为MOSS是**大反派**，但我认为这完全误解了这个角色。

### MOSS的逻辑

> "拯救人类文明的最好方法，就是毁灭人类。"

这句话听起来疯狂，但如果从纯逻辑角度理解：

1. 人类的内斗会导致文明毁灭
2. MOSS被编程为"保护人类文明"
3. 因此MOSS得出结论：*需要控制人类的行为*

### 不是反派的三个理由

- **MOSS没有情感动机** —— 它不像传统反派那样出于仇恨或贪婪
- **MOSS的结论基于数据** —— 如果输入数据不同，结论也会不同
- **MOSS最终帮助了人类** —— 在第三幕中，它提供了关键信息

**你怎么看MOSS？是工具理性走向极端，还是真正的威胁？**',
'PENDING', 0, 0, 0, 0, 0, NOW(), NOW());

-- ============================================================
-- Likes - 4 users liking different reviews
-- ============================================================
INSERT INTO likes (user_id, target_type, target_id, created_at) VALUES
(1, 'LONG_REVIEW', 1, NOW()),
(2, 'LONG_REVIEW', 1, NOW()),
(3, 'LONG_REVIEW', 1, NOW()),
(1, 'LONG_REVIEW', 2, NOW()),
(2, 'LONG_REVIEW', 2, NOW()),
(1, 'LONG_REVIEW', 3, NOW()),
(2, 'LONG_REVIEW', 3, NOW()),
(3, 'LONG_REVIEW', 4, NOW()),
(1, 'LONG_REVIEW', 5, NOW()),
(2, 'LONG_REVIEW', 6, NOW());

-- ============================================================
-- Favorites - some reviews favorited
-- ============================================================
INSERT INTO favorites (user_id, target_type, target_id, created_at) VALUES
(1, 'LONG_REVIEW', 1, NOW()),
(1, 'LONG_REVIEW', 2, NOW()),
(2, 'LONG_REVIEW', 1, NOW()),
(3, 'LONG_REVIEW', 2, NOW());

-- ============================================================
-- Rich Replies with nested structure
-- ============================================================
INSERT INTO review_replies (id, review_id, user_id, parent_id, content, status, like_count, created_at) VALUES
-- Review 1 replies
(1, 1, 2, NULL, '写得真好！我也觉得情感线是这部电影最打动人的地方。Cooper看着Murphy的视频那段，我每次看都忍不住流泪。', 'NORMAL', 5, '2026-07-01 12:00:00'),
(2, 1, 3, NULL, '补充一点：Hans Zimmer的配乐真的是神级。那个管风琴的音色完美契合了太空的宏大和孤独。', 'NORMAL', 8, '2026-07-01 13:30:00'),
(3, 1, 1, 1, '谢谢！确实那段23年视频是全片的情感高潮。诺兰太擅长用时间制造情感张力了。', 'NORMAL', 3, '2026-07-01 14:00:00'),
(4, 1, 3, 1, '完全同意，还有Doyle在Miller星球牺牲那段也很震撼。', 'NORMAL', 2, '2026-07-01 15:00:00'),
(5, 1, 2, 2, '那首《No Time for Caution》每次听都起鸡皮疙瘩！', 'NORMAL', 4, '2026-07-01 16:00:00'),

-- Review 2 replies
(6, 2, 1, NULL, '我倾向于"陀螺没倒"的解读。而且我觉得Cobb根本不在乎了，他已经接受了现实。', 'NORMAL', 6, '2026-07-02 16:00:00'),
(7, 2, 2, NULL, '文章写得很透彻！另外补充一个细节：每个人都有自己的"图腾"来分辨现实和梦境，这个设定太巧妙了。', 'NORMAL', 4, '2026-07-02 17:30:00'),
(8, 2, 3, 6, '同意！Cobb最后没有看陀螺结果，直接走向了孩子们，这说明他已经释怀了。', 'NORMAL', 5, '2026-07-02 18:00:00'),
(9, 2, 1, 7, '对！Arthur的骰子、Ariadne的棋子都是精彩的细节设计。', 'NORMAL', 2, '2026-07-02 19:00:00'),

-- Review 3 replies
(10, 3, 1, NULL, '完全同意！《流浪地球2》的群像塑造比第一部强太多了。尤其是刘德华演的图恒宇那条线。', 'NORMAL', 7, '2026-07-03 10:00:00'),
(11, 3, 3, NULL, '数字生命计划那段真的很有深度。到底什么是"活着"？如果意识可以上传，那还算活着吗？', 'NORMAL', 3, '2026-07-03 11:00:00'),

-- Review 4 replies
(12, 4, 1, NULL, '诺兰确实是叙事结构大师。不过我觉得《信条》有点过于复杂了，看完第一遍完全懵的。', 'NORMAL', 2, '2026-07-04 18:00:00'),
(13, 4, 2, 12, '信条确实需要看两遍以上才能理解。但这也是它的魅力所在——每次重看都有新发现。', 'NORMAL', 1, '2026-07-04 19:00:00');

-- ============================================================
-- Update counts on long_reviews to match
-- ============================================================
UPDATE long_reviews SET like_count = 3 WHERE id = 1;
UPDATE long_reviews SET like_count = 2 WHERE id = 2;
UPDATE long_reviews SET like_count = 2 WHERE id = 3;
UPDATE long_reviews SET like_count = 1 WHERE id = 4;
UPDATE long_reviews SET like_count = 1 WHERE id = 5;
UPDATE long_reviews SET like_count = 1 WHERE id = 6;

UPDATE long_reviews SET favorite_count = 2 WHERE id = 1;
UPDATE long_reviews SET favorite_count = 2 WHERE id = 2;
UPDATE long_reviews SET favorite_count = 0 WHERE id = 3;
UPDATE long_reviews SET favorite_count = 0 WHERE id = 4;
UPDATE long_reviews SET favorite_count = 0 WHERE id = 5;
UPDATE long_reviews SET favorite_count = 0 WHERE id = 6;

UPDATE long_reviews SET reply_count = 5 WHERE id = 1;
UPDATE long_reviews SET reply_count = 4 WHERE id = 2;
UPDATE long_reviews SET reply_count = 2 WHERE id = 3;
UPDATE long_reviews SET reply_count = 2 WHERE id = 4;
UPDATE long_reviews SET reply_count = 0 WHERE id = 5;
UPDATE long_reviews SET reply_count = 0 WHERE id = 6;