-- ============================================================
-- PPT代码展示 7 / 7 — 数据库核心表设计
-- 郭俊岑 负责：长评社区与讨论模块
-- ============================================================

-- 【我的设计思路】
-- 长评模块涉及 5 张核心表，按职责分为三组：

-- ① 内容主表（long_reviews）
--    存储长评标题、Markdown 正文、状态、各交互计数

-- ② 辅助内容表
--    review_images      — 长评配图（多张）
--    review_replies     — 长评回复（支持嵌套回复）

-- ③ 交互记录表（通用设计，通过 target_type + target_id 关联）
--    likes              — 点赞记录
--    favorites          — 收藏记录
--    reports            — 举报记录

-- 【核心设计决策】
-- 1. 内容计数冗余存储：like_count / reply_count 冗余在 long_reviews
--    表面上违反第三范式，实际是为了避免每次列表页都做 COUNT 子查询
-- 2. likes/favorites 使用 (user_id + target_type + target_id) 唯一索引
--    既保证了一个人不能对同一内容重复点赞/收藏，又提供了快速查询
-- 3. 软删除设计：所有表使用 deleted_at 字段，不真实删除数据
--    审核记录可追溯、用户行为可复盘
-- 4. 内容审核状态机：PENDING → APPROVED → ONLINE → OFFLINE → DELETED


-- ============================================================
-- ① 长评主表
-- ============================================================
CREATE TABLE IF NOT EXISTS long_reviews (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id      BIGINT NOT NULL COMMENT '关联电影ID',
    user_id       BIGINT NOT NULL COMMENT '作者ID',
    title         VARCHAR(100) NOT NULL COMMENT '长评标题',
    content_md    MEDIUMTEXT NOT NULL COMMENT 'Markdown正文',
    cover_url     VARCHAR(500) COMMENT '封面图URL',

    -- 审核状态：PENDING → APPROVED → ONLINE → OFFLINE → DELETED
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    -- 冗余计数（避免每次列表查询都COUNT子表）
    view_count    BIGINT NOT NULL DEFAULT 0 COMMENT '浏览量',
    like_count    INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    favorite_count INT NOT NULL DEFAULT 0 COMMENT '收藏数',
    reply_count   INT NOT NULL DEFAULT 0 COMMENT '回复数',

    -- 精选标记
    is_featured   TINYINT NOT NULL DEFAULT 0 COMMENT '是否精选',
    featured_at   DATETIME COMMENT '精选时间',

    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME COMMENT '软删除时间',

    -- 索引设计：按查询场景建索引
    INDEX idx_movie_status_created (movie_id, status, created_at),
    INDEX idx_user_id (user_id),
    INDEX idx_featured_status (is_featured, status)
) COMMENT='长评表';


-- ============================================================
-- ② 长评回复表（支持嵌套回复）
-- ============================================================
CREATE TABLE IF NOT EXISTS review_replies (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id     BIGINT NOT NULL COMMENT '所属长评ID',
    user_id       BIGINT NOT NULL COMMENT '回复用户ID',
    parent_id     BIGINT COMMENT '父回复ID（NULL=一级回复）',
    content       VARCHAR(1000) NOT NULL COMMENT '回复内容',
    status        VARCHAR(30) NOT NULL DEFAULT 'NORMAL',

    like_count    INT NOT NULL DEFAULT 0,
    report_count  INT NOT NULL DEFAULT 0,

    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME,

    INDEX idx_review_created (review_id, created_at)
) COMMENT='长评回复表';


-- ============================================================
-- ③ 点赞表（通用设计：通过 target_type 关联不同内容）
-- ============================================================
CREATE TABLE IF NOT EXISTS likes (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT NOT NULL,
    target_type   VARCHAR(50) NOT NULL COMMENT 'SHORT_COMMENT / LONG_REVIEW / REVIEW_REPLY',
    target_id     BIGINT NOT NULL COMMENT '目标ID',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 唯一索引：一人不能对同一内容重复点赞
    UNIQUE INDEX uk_user_target (user_id, target_type, target_id)
) COMMENT='点赞表';


-- ============================================================
-- ④ 收藏表（同样通过 target_type + target_id 关联）
-- ============================================================
CREATE TABLE IF NOT EXISTS favorites (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT NOT NULL,
    target_type   VARCHAR(50) NOT NULL DEFAULT 'LONG_REVIEW',
    target_id     BIGINT NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE INDEX uk_user_target (user_id, target_type, target_id)
) COMMENT='收藏表';


-- ============================================================
-- ⑤ 举报表
-- ============================================================
CREATE TABLE IF NOT EXISTS reports (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id   BIGINT NOT NULL COMMENT '举报人ID',
    target_type   VARCHAR(50) NOT NULL,
    target_id     BIGINT NOT NULL,
    reason        VARCHAR(255) NOT NULL COMMENT '举报原因',
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    handler_id    BIGINT COMMENT '处理人ID',
    handle_result VARCHAR(500) COMMENT '处理结果',
    handled_at    DATETIME COMMENT '处理时间',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT='举报表';


-- ============================================================
-- ⑥ 审核记录表（审计流水）
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_records (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    target_type   VARCHAR(50) NOT NULL,
    target_id     BIGINT NOT NULL,
    submitter_id  BIGINT COMMENT '投稿人',
    auditor_id    BIGINT NOT NULL COMMENT '审核人',
    action        VARCHAR(50) NOT NULL COMMENT 'SUBMIT/APPROVE/REJECT/PUBLISH/OFFLINE',
    before_status VARCHAR(30) COMMENT '操作前状态',
    after_status  VARCHAR(30) NOT NULL COMMENT '操作后状态',
    reject_reason VARCHAR(500) COMMENT '驳回原因',
    remark        VARCHAR(500) COMMENT '备注',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT='审核记录表';

<!--
【PPT讲解提示】
"最后一页展示的是我负责模块的数据库设计。
 核心思路是：内容存储与交互记录分离——
 long_reviews 存正文，likes 和 favorites 通过
 target_type + target_id 的通用关联模式支持多种内容类型的互动，
 未来扩展到资讯、视频等模块时不需要新建表。
 计数冗余是一个有意的设计取舍：like_count 存在主表上
 看起来违反了三范式，但避免了每次列表查询都做 COUNT 关联，
 在高并发场景下这是常见的性能优化手段。
 所有删除都用 deleted_at 软删除，保留了完整的审计追踪能力。"
-->