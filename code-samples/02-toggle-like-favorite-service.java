/**
 * ============================================================
 * PPT代码展示 2 / 7 — 点赞/收藏 Toggle 模式实现
 * 郭俊岑 负责：长评社区与讨论模块
 * ============================================================
 *
 * 【核心设计】为什么用 "Toggle" 而不是分离的 like / dislike 接口？
 *   分离接口会导致前端需要维护两个按钮的状态，
 *   而 Toggle 模式下，点击同一个接口，后端自动判断：
 *     - 如果用户已经赞过 → 删除点赞记录并 count-1
 *     - 如果用户没有赞过 → 新增点赞记录并 count+1
 *   这样前端只需一个按钮、一次请求，用户体验更好。
 *
 * 【并发安全】使用 Math.max(0, count-1) 防止并发下计数变负
 * 【事务管理】@Transactional 确保点赞记录和计数更新同时成功或失败
 */

@Service
@RequiredArgsConstructor
public class LongReviewServiceImpl implements LongReviewService {

    /**
     * Toggle 点赞（同接口即点即消）
     * 
     * 设计要点：
     * ① 先查询用户是否已有点赞记录（userId + targetType + targetId 唯一）
     * ② 如果存在 → DELETE 记录 + likeCount-1（防止并发为负用 Math.max）
     * ③ 如果不存在 → INSERT 新记录 + likeCount+1
     * ④ @Transactional 保证两表写入的原子性
     */
    @Override
    @Transactional
    public void likeReview(Long reviewId, Long userId) {
        // 1. 校验长评是否存在
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }

        // 2. 查询用户是否已点赞（userId + targetType = LONG_REVIEW + targetId）
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId)
               .eq(LikeRecord::getTargetType, TargetTypeEnum.LONG_REVIEW.name())
               .eq(LikeRecord::getTargetId, reviewId);
        LikeRecord existing = likeRecordMapper.selectOne(wrapper);

        // 3. Toggle 逻辑：有则删，无则增
        if (existing != null) {
            // 取消点赞
            likeRecordMapper.deleteById(existing.getId());
            // 防御性编程：防止并发下计数为负
            review.setLikeCount(Math.max(0, review.getLikeCount() - 1));
        } else {
            // 添加点赞
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
            record.setTargetId(reviewId);
            likeRecordMapper.insert(record);
            review.setLikeCount(review.getLikeCount() + 1);
        }
        // 4. 更新长评计数
        longReviewMapper.updateById(review);
    }

    /**
     * Toggle 收藏（同样的 Toggle 模式）
     * 逻辑与点赞完全一致，操作 favorites 表
     */
    @Override
    @Transactional
    public void favoriteReview(Long reviewId, Long userId) {
        // ... 同样的 Toggle 逻辑，写 favorites 表
        // 不同之处：targetType 虽然目前只有 LONG_REVIEW，
        // 但设计上预留了扩展性，未来可以收藏资讯、商品等
    }
}

/**
 * 【PPT讲解提示】
 * "这段代码展示了点赞功能的 Toggle 设计。
 *  core 逻辑在第185-196行：先用 LambdaQueryWrapper 构建查询条件，
 *  判断用户是否已经点过赞，如果点过则删除记录并把计数减1，
 *  没点过则新增记录把计数加1。
 *  Math.max(0, n-1) 是为了防止并发情况下计数变成负数，
 *  这是一个防御性编程的细节。
 *  @Transactional 注解保证了 likes 表和 long_reviews 表的写入
 *  要么同时成功，要么同时回滚。"
 */