/**
 * ============================================================
 * PPT代码展示 1 / 7 — 长评模块 REST API 控制器
 * 郭俊岑 负责：长评社区与讨论模块
 * ============================================================
 *
 * 【核心问题】前端需要哪些接口来满足长评社区的完整交互？
 *   ① 长评列表（支持按电影筛选、按热度/时间排序、分页）
 *   ② 长评详情（Markdown正文 + 图片 + 互动状态）
 *   ③ 发布/修改长评（提交后进入审核流程）
 *   ④ 点赞 / 收藏 / 举报（Toggle 模式：同接口即赞即消）
 *   ⑤ 回复列表 + 发表回复 + 回复点赞
 *
 * 【我的设计思路】
 *   - 每个接口职责单一，路径按 RESTful 规范设计
 *   - 点赞和收藏采用"Toggle"模式：第二次调用取消操作
 *     这样前端只需一个按钮，后端处理幂等性
 *   - 所有接口统一返回 ApiResponse<T> 包装，前端无需判断状态码
 */
package com.example.movie.longreview.controller;

// ==================== 核心接口一览 ====================

@RestController
@RequestMapping("/api/long-reviews")
@RequiredArgsConstructor
public class LongReviewController {

    // ① 长评列表：支持 movieId 筛选 + sortBy 排序 + 分页
    @GetMapping
    public ApiResponse<PageResult<LongReviewVO>> getReviewList(
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize);

    // ② 长评详情：返回 Markdown 全文 + 用户的互动状态
    @GetMapping("/{reviewId}")
    public ApiResponse<LongReviewVO> getReviewDetail(@PathVariable Long reviewId);

    // ③ 发布长评：接收 Markdown 内容，状态初始化为 PENDING
    @PostMapping
    public ApiResponse<Map<String, Long>> createReview(
            @Valid @RequestBody CreateReviewRequest request);

    // ④ 点赞/取消点赞（Toggle 模式）
    @PostMapping("/{reviewId}/like")
    public ApiResponse<Void> likeReview(@PathVariable Long reviewId);

    // ⑤ 收藏/取消收藏（Toggle 模式）
    @PostMapping("/{reviewId}/favorite")
    public ApiResponse<Void> favoriteReview(@PathVariable Long reviewId);

    // ⑥ 举报长评
    @PostMapping("/{reviewId}/report")
    public ApiResponse<Void> reportReview(@PathVariable Long reviewId,
                                           @RequestBody Map<String, String> body);

    // ⑦ 回复列表（一级回复 + 子回复）
    @GetMapping("/{reviewId}/replies")
    public ApiResponse<PageResult<ReviewReplyVO>> getReplies(...);

    // ⑧ 发表回复
    @PostMapping("/{reviewId}/replies")
    public ApiResponse<Map<String, Long>> createReply(...);

    // ⑨ 回复点赞
    @PostMapping("/replies/{replyId}/like")
    public ApiResponse<Void> likeReply(@PathVariable Long replyId);
}

/**
 * 【PPT讲解提示】
 * "这个 Controller 是长评模块的入口层，9个REST接口覆盖了
 *  从列表浏览、详情阅读到互动操作的全部功能。
 *  我采用 Spring MVC 的 @RestController 注解，
 *  统一返回 ApiResponse 包装类来规范响应格式。
 *  点赞和收藏使用同一个 POST 接口实现 Toggle，
 *  即点击一次是添加，再点击是取消，由后端判断当前状态。"
 */