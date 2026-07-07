# 电影点评系统接口 API 规范文档

## 1. 接口约定

### 1.1 基础路径

建议基础路径：

```text
/api
```

管理端接口统一使用：

```text
/api/admin
```

电影商后台接口统一使用：

```text
/api/official
```

### 1.2 请求格式

- GET 请求参数通过 query 传递。
- POST、PUT、PATCH 请求参数通过 JSON body 传递。
- 文件上传使用 multipart/form-data。
- 需要登录的接口通过 Authorization 请求头传递 Token。

```text
Authorization: Bearer <token>
```

### 1.3 统一响应格式

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

分页响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [],
    "page": 1,
    "pageSize": 10,
    "total": 0
  }
}
```

### 1.4 通用错误码

| code | 含义 |
| --- | --- |
| 0 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或 Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 数据冲突 |
| 500 | 服务器错误 |

## 2. 认证与用户接口

### 2.1 用户注册

```text
POST /api/auth/register
```

请求体：

```json
{
  "username": "user001",
  "password": "123456",
  "nickname": "电影爱好者"
}
```

### 2.2 用户登录

```text
POST /api/auth/login
```

请求体：

```json
{
  "username": "user001",
  "password": "123456"
}
```

返回：

```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "username": "user001",
    "nickname": "电影爱好者",
    "roles": ["USER"]
  }
}
```

### 2.3 退出登录

```text
POST /api/auth/logout
```

### 2.4 获取当前用户信息

```text
GET /api/users/me
```

### 2.5 修改个人资料

```text
PUT /api/users/me
```

## 3. 首页接口

### 3.1 首页聚合数据

```text
GET /api/home
```

返回内容包括：

- 热门资讯轮播。
- 电影资讯栏目。
- 热门电影。
- 高分电影。
- 最新电影。
- 优质长评。
- 推荐周边。

### 3.2 指定推荐位列表

```text
GET /api/home/recommendations?sectionCode=BANNER_NEWS
```

## 4. 电影接口

### 4.1 电影搜索

```text
GET /api/movies
```

查询参数：

| 参数 | 说明 |
| --- | --- |
| keyword | 电影名称、导演、演员关键词 |
| genre | 类型 |
| releaseYear | 上映年份 |
| page | 页码 |
| pageSize | 每页数量 |

### 4.2 电影详情

```text
GET /api/movies/{movieId}
```

详情页应返回：

- 电影基本信息。
- 总评分和细分评分。
- 短评列表摘要。
- 长评入口或长评摘要。
- 相关资讯。
- 解读视频。
- 周边商品。

### 4.3 管理员新增电影

```text
POST /api/admin/movies
```

### 4.4 管理员修改电影

```text
PUT /api/admin/movies/{movieId}
```

### 4.5 管理员删除电影

```text
DELETE /api/admin/movies/{movieId}
```

## 5. 评分接口

### 5.1 提交或更新评分

```text
POST /api/movies/{movieId}/ratings
```

请求体：

```json
{
  "totalScore": 9.0,
  "storyScore": 8.5,
  "visualScore": 9.5,
  "actingScore": 8.0
}
```

规则：

- totalScore 必填。
- storyScore、visualScore、actingScore 选填。
- 同一用户对同一电影重复提交时更新原评分。

### 5.2 获取我的评分

```text
GET /api/movies/{movieId}/ratings/me
```

### 5.3 获取电影评分统计

```text
GET /api/movies/{movieId}/ratings/summary
```

## 6. 短评接口

### 6.1 短评列表

```text
GET /api/movies/{movieId}/short-comments?page=1&pageSize=10
```

### 6.2 发布短评

```text
POST /api/movies/{movieId}/short-comments
```

请求体：

```json
{
  "content": "节奏很好，值得一看。"
}
```

### 6.3 点赞短评

```text
POST /api/short-comments/{commentId}/like
```

### 6.4 取消点赞短评

```text
DELETE /api/short-comments/{commentId}/like
```

### 6.5 举报短评

```text
POST /api/short-comments/{commentId}/report
```

请求体：

```json
{
  "reason": "包含违规内容"
}
```

### 6.6 管理员删除短评

```text
DELETE /api/admin/short-comments/{commentId}
```

## 7. 长评与讨论接口

### 7.1 长评列表

```text
GET /api/long-reviews
```

查询参数：

| 参数 | 说明 |
| --- | --- |
| movieId | 电影 ID |
| keyword | 标题关键词 |
| sort | hot, latest |
| page | 页码 |
| pageSize | 每页数量 |

### 7.2 长评详情

```text
GET /api/long-reviews/{reviewId}
```

### 7.3 提交长评

```text
POST /api/long-reviews
```

请求体：

```json
{
  "movieId": 1,
  "title": "这部电影的主题表达",
  "contentMd": "## 标题\n正文内容",
  "coverUrl": "https://example.com/cover.jpg"
}
```

提交后状态为 PENDING。

### 7.4 修改长评草稿或被驳回长评

```text
PUT /api/long-reviews/{reviewId}
```

### 7.5 点赞长评

```text
POST /api/long-reviews/{reviewId}/like
```

### 7.6 收藏长评

```text
POST /api/long-reviews/{reviewId}/favorite
```

### 7.7 举报长评

```text
POST /api/long-reviews/{reviewId}/report
```

### 7.8 长评回复列表

```text
GET /api/long-reviews/{reviewId}/replies
```

### 7.9 发布长评回复

```text
POST /api/long-reviews/{reviewId}/replies
```

请求体：

```json
{
  "parentId": null,
  "content": "这个角度很有意思。"
}
```

### 7.10 管理员设置优质长评

```text
POST /api/admin/long-reviews/{reviewId}/feature
```

### 7.11 管理员取消优质长评

```text
DELETE /api/admin/long-reviews/{reviewId}/feature
```

## 8. 资讯接口

### 8.1 资讯列表

```text
GET /api/news
```

查询参数：

| 参数 | 说明 |
| --- | --- |
| keyword | 标题关键词 |
| category | 分类 |
| movieId | 关联电影 |
| page | 页码 |
| pageSize | 每页数量 |

### 8.2 资讯详情

```text
GET /api/news/{newsId}
```

### 8.3 管理员新增资讯

```text
POST /api/admin/news
```

### 8.4 管理员修改资讯

```text
PUT /api/admin/news/{newsId}
```

### 8.5 管理员删除资讯

```text
DELETE /api/admin/news/{newsId}
```

## 9. 解读视频接口

### 9.1 电影相关视频列表

```text
GET /api/movies/{movieId}/videos
```

### 9.2 视频点击统计

```text
POST /api/videos/{videoId}/click
```

### 9.3 管理员管理视频

```text
POST /api/admin/videos
PUT /api/admin/videos/{videoId}
DELETE /api/admin/videos/{videoId}
```

## 10. 周边商城接口

### 10.1 商品列表

```text
GET /api/merchandise
```

查询参数：

| 参数 | 说明 |
| --- | --- |
| keyword | 商品名或电影名 |
| movieId | 所属电影 |
| productType | 商品类型 |
| minPrice | 最低价格 |
| maxPrice | 最高价格 |
| page | 页码 |
| pageSize | 每页数量 |

### 10.2 商品详情

```text
GET /api/merchandise/{productId}
```

### 10.3 商品点击跳转统计

```text
POST /api/merchandise/{productId}/click
```

### 10.4 管理员管理商品

```text
POST /api/admin/merchandise
PUT /api/admin/merchandise/{productId}
DELETE /api/admin/merchandise/{productId}
```

## 11. 电影商后台接口

### 11.1 提交认证申请

```text
POST /api/official/certification
```

请求体：

```json
{
  "companyName": "某某电影公司",
  "contactName": "张三",
  "contactPhone": "13800000000",
  "licenseUrl": "https://example.com/license.jpg"
}
```

### 11.2 查看认证状态

```text
GET /api/official/certification
```

### 11.3 电影商投稿电影信息

```text
POST /api/official/movies
```

### 11.4 电影商投稿资讯

```text
POST /api/official/news
```

### 11.5 电影商投稿视频链接

```text
POST /api/official/videos
```

### 11.6 电影商投稿周边商品

```text
POST /api/official/merchandise
```

### 11.7 查看我的投稿列表

```text
GET /api/official/submissions?type=MOVIE&status=PENDING
```

### 11.8 查看投稿审核记录

```text
GET /api/official/submissions/{targetType}/{targetId}/audit-records
```

## 12. 通用审核接口

### 12.1 待审核列表

```text
GET /api/admin/audits
```

查询参数：

| 参数 | 说明 |
| --- | --- |
| targetType | MOVIE, NEWS, VIDEO, MERCHANDISE, LONG_REVIEW, OFFICIAL_CERTIFICATION |
| status | PENDING, APPROVED, ONLINE, REJECTED, OFFLINE |
| submitterId | 投稿人 ID |
| page | 页码 |
| pageSize | 每页数量 |

### 12.2 审核通过

```text
POST /api/admin/audits/{targetType}/{targetId}/approve
```

### 12.3 驳回

```text
POST /api/admin/audits/{targetType}/{targetId}/reject
```

请求体：

```json
{
  "rejectReason": "图片不清晰，请重新上传。"
}
```

### 12.4 上线

```text
POST /api/admin/audits/{targetType}/{targetId}/publish
```

### 12.5 下架

```text
POST /api/admin/audits/{targetType}/{targetId}/offline
```

请求体：

```json
{
  "reason": "内容过期"
}
```

### 12.6 审核记录

```text
GET /api/admin/audit-records
```

## 13. 管理员后台接口

### 13.1 后台概览

```text
GET /api/admin/dashboard
```

### 13.2 用户列表

```text
GET /api/admin/users
```

### 13.3 封禁用户

```text
POST /api/admin/users/{userId}/ban
```

### 13.4 解除封禁

```text
POST /api/admin/users/{userId}/unban
```

### 13.5 首页推荐位列表

```text
GET /api/admin/homepage-recommendations
```

### 13.6 新增首页推荐位

```text
POST /api/admin/homepage-recommendations
```

### 13.7 修改首页推荐位

```text
PUT /api/admin/homepage-recommendations/{id}
```

### 13.8 删除首页推荐位

```text
DELETE /api/admin/homepage-recommendations/{id}
```

### 13.9 举报列表

```text
GET /api/admin/reports
```

### 13.10 处理举报

```text
POST /api/admin/reports/{reportId}/handle
```

请求体：

```json
{
  "result": "已删除违规内容",
  "targetAction": "DELETE"
}
```

## 14. AI 助手接口

### 14.1 创建会话

```text
POST /api/ai/sessions
```

### 14.2 发送消息

```text
POST /api/ai/chat
```

请求体：

```json
{
  "sessionId": 1,
  "message": "推荐几部高分科幻电影"
}
```

返回：

```json
{
  "reply": "可以看看以下电影...",
  "recommendations": [
    {
      "type": "MOVIE",
      "id": 1,
      "title": "星际穿越",
      "url": "/movies/1"
    }
  ]
}
```

## 15. 文件上传接口

### 15.1 上传图片

```text
POST /api/uploads/images
```

用途：

- 用户头像。
- 电影海报。
- 长评图片。
- 资讯封面。
- 视频封面。
- 周边商品图片。
- 电影商认证材料。

限制：

- 文件类型：jpg、jpeg、png、webp。
- 单文件大小建议不超过 5MB。

