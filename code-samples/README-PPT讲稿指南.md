# 郭俊岑 — 长评社区与讨论模块 答辩PPT代码展示指南

> 共 7 页代码片段 + 每页对应讲解词，可直接截图放入PPT并照稿讲解。
> 所有代码均来自实际项目（movie-server 和 movie-web），经过注释精简便于理解。

---

## 第1页：REST API 设计（LongReviewController）
**文件名：** `01-LongReviewController-REST-API.java`

### 讲解词（约45秒）：
"这是我负责的长评模块的 REST API 控制器。我设计了9个接口来覆盖整个长评社区的功能需求。
从上到下分别是：长评列表（支持按电影筛选和热度/时间排序）、长评详情（返回Markdown全文）、
发布长评、修改长评、点赞、收藏、举报、回复列表、发表回复以及回复点赞。
所有接口统一返回 ApiResponse 包装类，前端不需要判断各种状态码。
点赞和收藏采用了 Toggle 模式——同一接口第一次调用是添加，第二次是取消，
这样前端只需要一个按钮就能完成赞/消赞的切换。"

---

## 第2页：点赞 Toggle 模式（LongReviewServiceImpl）
**文件名：** `02-toggle-like-favorite-service.java`

### 讲解词（约45秒）：
"这一页展示的是点赞功能的核心实现逻辑。我采用了 Toggle 设计模式——
先通过 LambdaQueryWrapper 构建查询条件，判断用户是否已经点赞过。
如果点过了就删除记录、计数减1；没点过就新增记录、计数加1。
注意这里用了 Math.max(0, n-1) 来做防御性编程——在并发场景下，
如果多个请求同时取消点赞，计数不会变成负数。
整个方法加了 @Transactional 注解，保证 likes 表和 long_reviews 表的写入
是原子操作——要么都成功要么都失败，不会出现记录删了但计数没减的数据不一致问题。"

---

## 第3页：MyBatis 多表关联 SQL（LongReviewMapper.xml）
**文件名：** `03-longreview-mapper-sql.xml`

### 讲解词（约50秒）：
"这一页是我写的 MyBatis SQL 映射文件，是模块查询性能的关键。
我通过4个 LEFT JOIN 把 long_reviews、movies、users、likes、favorites
这五张表的数据合并到一条 SQL 里查询，避免了 N+1 次查询的性能问题。
其中 CASE WHEN 子句是亮点——它在同一条 SQL 中判断了当前用户是否已点赞或已收藏，
前端拿到布尔值直接控制按钮的高亮状态，不需要额外发请求。
热度排序公式我给了收藏3倍权重、回复2倍权重，
因为收藏和回复的交互成本比单纯浏览更高，更能代表内容质量。
动态排序用了 MyBatis 的 choose/when 标签，前端传什么参数就怎么排。"

---

## 第4页：发布长评 + 审核流（LongReviewServiceImpl）
**文件名：** `04-create-review-with-audit.java`

### 讲解词（约45秒）：
"这段代码展示了'写长评'功能的后端实现——它不是简单的单表 INSERT，
而是一个三表联动的原子事务。
第一步，把标题和 Markdown 正文写入 long_reviews 主表，
状态初始化为 PENDING（待审核）；
第二步，如果用户上传了配图，批量写入 review_images 表并保留图片顺序；
第三步，在 audit_records 表写入一条审核流水，记录'谁提交了什么内容'。
@Transactional 保证了这三步要么全部成功要么全部回滚，
不会出现主表写入了但配图丢失的半成品状态。
内容初始化为 PENDING 体现了审核设计理念——所有用户生产的内容
都需要管理员审核后才能上线对公众可见。"

---

## 第5页：前端 Markdown 渲染（LongReviewDetailPage.vue）
**文件名：** `05-frontend-markdown-detail.vue`

### 讲解词（约50秒）：
"这一页展示的是前端长评详情页的 Markdown 渲染实现。
我用了 marked 和 highlight.js 两个第三方库——
marked 负责把 Markdown 文本转成 HTML，highlight.js 负责代码块的语法高亮。
关键设计是注册了 marked 的自定义 renderer，在代码块渲染时注入语言类名，
highlight.js 就能自动识别并高亮。
Vue 的 computed 属性实现了响应式渲染——后端返回的新内容一到，
页面自动刷新，不需要手动触发。
样式方面，marked 生成的 HTML 没有 Vue 的 scoped 属性，
所以我在所有选择器前面加了 :deep() 伪类来穿透 scoped 隔离，
给标题、引用块、代码块加上统一的'黑金'主题风格。
最终效果是：用户在文本域里写 Markdown，页面上立刻呈现排版精致的文章。"

---

## 第6页：管理员审核页面（AuditListPage.vue）
**文件名：** `06-frontend-admin-audit.vue`

### 讲解词（约45秒）：
"这一页是管理员审核长评的前端实现。上面有三个 Tab 栏用来切换
待审核、已驳回、已上线三种状态。
核心设计是'根据内容状态动态渲染操作按钮'——
待审核的显示通过和驳回，已上线的显示下架，已删除的不显示操作按钮。
驳回时弹出模态框要求管理员填写驳回原因，前端做了非空校验才允许提交。
删除操作使用 Element Plus 库的 ElMessageBox.confirm 进行二次确认，
防止误操作。
整个页面的交互逻辑都是调用我在 longReviewApi.ts 中封装的接口，
前端不直接拼 SQL 也不拼接 URL，保证了接口层的统一管理。"

---

## 第7页：数据库设计（表结构 DDL）
**文件名：** `07-database-design.sql`

### 讲解词（约50秒）：
"最后一页是我负责模块的数据库设计。核心设计思路是内容与交互分离——
long_reviews 存正文和基础信息，likes、favorites 这些交互通过
target_type + target_id 的通用关联模式来支持不同类型内容的互动，
未来扩展到资讯、视频等模块时不需要建新表，改枚举就行。
计数字段（like_count、reply_count）冗余在主表上
是我有意做的性能优化——虽然表面上违反了三范式，
但避免了每次列表查询都去子表做 COUNT 关联，
在高并发场景下这是常见的'以空间换时间'策略。
所有表都用了 deleted_at 做软删除，不真实删数据，
保证审核记录可追溯、用户行为可复盘。
likes 和 favorites 表都加了 (user_id, target_type, target_id) 的唯一索引，
从数据库层面保证了一个人不能对同一内容重复点赞或收藏。"

---

## PPT 演示顺序建议

| 页 | 主题 | 层次 |
|----|------|------|
| 1 | REST API | 宏观：展示接口全貌 |
| 7 | 数据库设计 | 底层：数据结构怎么存的 |
| 4 | 发布 + 审核流 | 业务核心：三表联动的原子事务 |
| 2 | 点赞/收藏 Toggle | 技术细节：并发安全的巧思 |
| 3 | MyBatis SQL | 性能关键：多表关联一行搞定 |
| 5 | 前端 Markdown | 用户体验：图文并茂的展示 |
| 6 | 管理员审核 | 完整性：完整闭环 |

**总计讲解时间：约 5-6 分钟**