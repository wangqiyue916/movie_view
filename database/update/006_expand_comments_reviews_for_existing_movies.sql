SET NAMES utf8mb4;
SET time_zone = '+08:00';
USE `movie`;

START TRANSACTION;

-- 为当前 movies 表中的在线电影补充演示短评与长评。
-- 内容为原创演示文本；脚本按 movie_id 生成固定 id，可重复执行，不会无限追加。

DELETE FROM `review_images` WHERE review_id BETWEEN 20000 AND 29999;
DELETE FROM `review_replies` WHERE review_id BETWEEN 20000 AND 29999;
DELETE FROM `likes` WHERE target_type = 'LONG_REVIEW' AND target_id BETWEEN 20000 AND 29999;
DELETE FROM `favorites` WHERE target_type = 'LONG_REVIEW' AND target_id BETWEEN 20000 AND 29999;
DELETE FROM `long_reviews` WHERE id BETWEEN 20000 AND 29999;

DELETE FROM `likes` WHERE target_type = 'SHORT_COMMENT' AND target_id BETWEEN 200000 AND 299999;
DELETE FROM `reports` WHERE target_type = 'SHORT_COMMENT' AND target_id BETWEEN 200000 AND 299999;
DELETE FROM `short_comments` WHERE id BETWEEN 200000 AND 299999;

INSERT INTO `short_comments` (id, movie_id, user_id, content, status, like_count, report_count, created_at)
SELECT 200000 + m.id * 10 + 1,
       m.id,
       101,
       CONCAT('补充短评：', m.title, ' 的整体完成度很稳，最值得看的是人物关系和主题表达，适合看完后继续读长评。'),
       'NORMAL',
       6 + MOD(m.id, 9),
       0,
       NOW()
FROM `movies` m
WHERE m.status = 'ONLINE' AND m.deleted_at IS NULL
ON DUPLICATE KEY UPDATE content = VALUES(content), like_count = VALUES(like_count);

INSERT INTO `short_comments` (id, movie_id, user_id, content, status, like_count, report_count, created_at)
SELECT 200000 + m.id * 10 + 2,
       m.id,
       102,
       CONCAT('二刷会更注意《', m.title, '》里的细节，导演、表演和节奏之间的配合比第一眼看到的更丰富。'),
       'NORMAL',
       5 + MOD(m.id, 8),
       0,
       NOW()
FROM `movies` m
WHERE m.status = 'ONLINE' AND m.deleted_at IS NULL
ON DUPLICATE KEY UPDATE content = VALUES(content), like_count = VALUES(like_count);

INSERT INTO `short_comments` (id, movie_id, user_id, content, status, like_count, report_count, created_at)
SELECT 200000 + m.id * 10 + 3,
       m.id,
       103,
       CONCAT('喜欢《', m.title, '》不是因为它只有爽点，而是看完以后还有很多地方值得回想和讨论。'),
       'NORMAL',
       4 + MOD(m.id, 7),
       0,
       NOW()
FROM `movies` m
WHERE m.status = 'ONLINE' AND m.deleted_at IS NULL
ON DUPLICATE KEY UPDATE content = VALUES(content), like_count = VALUES(like_count);

INSERT INTO `long_reviews` (
  id, movie_id, user_id, title, content_md, cover_url, status,
  view_count, like_count, favorite_count, reply_count, is_featured, featured_at, created_at, updated_at
)
SELECT
  20000 + m.id,
  m.id,
  104,
  CONCAT('《', m.title, '》补充长评：从观感到讨论价值'),
  CONCAT(
'## 为什么《', m.title, '》值得被认真讨论

这篇补充长评用于丰富电影详情页内容，重点不是简单判断“好看”或“不好看”，而是把一次观影体验拆成几个可以继续交流的角度。对电影点评系统来说，一部电影如果只有评分和几句短评，用户很难真正停留；而当详情页里有结构完整的长评、图片和讨论入口时，电影就会从资料条目变成社区话题。

![电影氛围图](', COALESCE(m.poster_url, '/merch-shubiao.png'), ')

### 一、基础观感：类型、节奏和人物

《', m.title, '》的第一层吸引力来自类型完成度。它的类型标签可以概括为：', COALESCE(m.genres, '剧情'), '。但真正决定观感的，并不是标签本身，而是影片如何把人物、冲突和节奏组织起来。观众进入电影时往往先被情节推动，等到看完之后，留下来的却常常是人物选择、情绪余味和几个关键场景。

- 导演：', COALESCE(m.director, '未知导演'), '
- 主演：', COALESCE(m.actors, '演员信息待补充'), '
- 地区：', COALESCE(m.region, '地区待补充'), '
- 上映时间：', COALESCE(DATE_FORMAT(m.release_date, '%Y-%m-%d'), '上映时间待补充'), '

### 二、为什么短评不够，长评仍然必要

短评适合记录即时感受，比如“节奏很好”“表演动人”“结尾有余味”。但长评的价值在于把这些感受重新整理成论证：为什么节奏好，表演好在哪里，结尾为什么会让人回想。用户在详情页阅读长评时，不只是接收观点，也是在寻找自己观影感受的表达方式。

| 讨论角度 | 可以展开的问题 |
|---|---|
| 剧情结构 | 冲突是否清晰，转折是否自然，结尾是否完成主题 |
| 人物塑造 | 主角的选择是否可信，配角是否推动主题 |
| 视听语言 | 色彩、构图、音乐和剪辑是否服务情绪 |
| 社区互动 | 这部电影是否适合继续回复、收藏和二次讨论 |

> 好的长评不是替观众下结论，而是帮助观众把模糊感受变成可以交流的语言。

### 三、放在电影详情页中的价值

如果用户从首页点击电影海报进入详情页，最先看到的是海报、简介和评分；继续往下看时，短评提供热度，长评提供深度。对于《', m.title, '》这样的条目，至少保留一篇结构完整的长评，可以让页面显得更真实，也方便后续对接资讯、解读视频和周边模块。

### 四、后续可以继续补充什么

后续成员可以基于这篇长评继续扩展：加入更具体的场景分析，补充幕后资料，或者将同导演、同演员、同类型影片进行横向比较。这样一来，长评不只是静态内容，而能成为用户讨论、收藏和推荐的起点。

**总体来说，《', m.title, '》适合作为电影详情页的内容样本：它既能展示评分，也能承载短评、长评、资讯和视频之间的联动。**'
  ),
  COALESCE(m.poster_url, '/merch-shubiao.png'),
  'ONLINE',
  180 + MOD(m.id, 80),
  18 + MOD(m.id, 20),
  5 + MOD(m.id, 8),
  2 + MOD(m.id, 5),
  CASE WHEN m.id IN (
      SELECT movie_id
      FROM (
        SELECT movie_id
        FROM `long_reviews`
        WHERE status = 'ONLINE' AND deleted_at IS NULL
        GROUP BY movie_id
        HAVING COUNT(*) = 0
      ) missing_movies
    ) THEN 1 ELSE 0 END,
  NULL,
  NOW(),
  NOW()
FROM `movies` m
WHERE m.status = 'ONLINE' AND m.deleted_at IS NULL
ON DUPLICATE KEY UPDATE
  title = VALUES(title),
  content_md = VALUES(content_md),
  cover_url = VALUES(cover_url),
  status = VALUES(status),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO `review_images` (review_id, image_url, sort_order)
SELECT 20000 + m.id,
       COALESCE(m.poster_url, '/merch-shubiao.png'),
       1
FROM `movies` m
WHERE m.status = 'ONLINE' AND m.deleted_at IS NULL
ON DUPLICATE KEY UPDATE image_url = VALUES(image_url), sort_order = VALUES(sort_order);

COMMIT;
