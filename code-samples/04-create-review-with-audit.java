/**
 * ============================================================
 * PPT代码展示 4 / 7 — 发布长评 + 审核流写入
 * 郭俊岑 负责：长评社区与讨论模块
 * ============================================================
 *
 * 【核心业务】用户发布长评后，如何进入审核流程？
 *   发布长评不是简单的单表 INSERT，而是"三表联动"的事务操作：
 *     ① long_reviews 表：保存正文、标题、状态初始为 PENDING
 *     ② review_images 表：保存配图（如果用户上传了图片）
 *     ③ audit_records 表：记录一条审核流水（action=SUBMIT）
 *   三张表的写入必须在同一个 @Transactional 中完成，
 *   任意一步失败则全部回滚。
 *
 * 【状态机思想】
 *   新增长评统一初始化为 PENDING 状态，不直接上线。
 *   审核通过后管理员将其改为 APPROVED → ONLINE。
 *   这保证了所有内容都经过人工审核才能被公众看到。
 */

@Service
@RequiredArgsConstructor
public class LongReviewServiceImpl {

    /**
     * 发布长评（三表联动写入）
     */
    @Override
    @Transactional  // ← 关键：保证三表写入的原子性
    public Long createReview(CreateReviewRequest request, Long userId) {

        // === 第一步：写入 long_reviews 主表 ===
        LongReview review = new LongReview();
        review.setMovieId(request.getMovieId());
        review.setUserId(userId);
        review.setTitle(request.getTitle());
        review.setContentMd(request.getContentMd()); // Markdown 原文存储
        review.setCoverUrl(request.getCoverUrl());
        review.setStatus(AuditStatusEnum.PENDING.name()); // 状态初始化：待审核
        review.setViewCount(0L);
        review.setLikeCount(0);
        review.setFavoriteCount(0);
        review.setReplyCount(0);
        review.setIsFeatured(0);
        longReviewMapper.insert(review); // Insert 后 MyBatis-Plus 回填自增 ID

        // === 第二步：如果有配图，批量写入 review_images 表 ===
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ReviewImage image = new ReviewImage();
                image.setReviewId(review.getId());
                image.setImageUrl(request.getImages().get(i));
                image.setSortOrder(i);  // 保留图片顺序
                reviewImageMapper.insert(image);
            }
        }

        // === 第三步：写入审核记录 audit_records 表 ===
        AuditRecord audit = new AuditRecord();
        audit.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
        audit.setTargetId(review.getId());
        audit.setSubmitterId(userId);
        audit.setAction("SUBMIT");          // 动作：提交
        audit.setAfterStatus(AuditStatusEnum.PENDING.name()); // 目标状态
        auditRecordMapper.insert(audit);

        return review.getId(); // 返回新创建的长评ID，前端据此跳转详情页
    }
}

/**
 * 【PPT讲解提示】
 * "这段代码展示了发布长评的核心逻辑，它不是简单的 insert，
 *  而是一个三表联动的原子事务。
 *  第一，主表 long_reviews 保存 Markdown 正文，状态初始为 PENDING；
 *  第二，如果用户上传了图片，批量写入 review_images 表；
 *  第三，在 audit_records 表记录一条审核流水。
 *  @Transactional 注解保证了这三步要么全部成功要么全部回滚，
 *  不会出现只有主表写入成功而配图丢失的情况。
 *  状态初始化为 PENDING 体现了内容审核的设计理念——
 *  所有用户发布的内容都需要管理员审核后才能上线。"
 */