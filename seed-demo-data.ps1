# Seed demo data for thesis defense demo
# All 7 reviews with rich Markdown content, likes, favorites, replies

$base = "http://localhost:8080/api"

# Helper function
function Post($url, $body) {
    $json = $body | ConvertTo-Json -Compress -Depth 1
    try {
        Invoke-RestMethod -Uri $url -Method Post -ContentType "application/json; charset=utf-8" -Body $json -TimeoutSec 5 | Out-Null
    } catch { Write-Host "WARN: $url failed: $_" }
}

# Delete existing data (via admin endpoints)
Write-Host "Cleaning old data..."
# We'll just overwrite via inserts, skip delete

# ===== Review 1 =====
Write-Host "Creating review 1..."
$body1 = @{
    movieId = 1
    title = "穿越星际之后，仍然回到人的情感"
    contentMd = @"
## 关于《星际穿越》

这部电影最动人的地方，是它把**宏大的宇宙尺度**和亲情联系在一起。

### 为什么值得反复观看

真正让人反复回看的，并不只是视觉奇观，还有：

1. **父女情感线** —— Cooper和Murphy之间的时间错位感
2. **科学真实性** —— 虫洞、黑洞、时间膨胀的物理理论背书
3. **配乐的力量** —— Hans Zimmer用管风琴营造的宏大氛围

> "We used to look up at the sky and wonder at our place in the stars. Now we just look down, and worry about our place in the dirt."

### 总结评分

| 维度 | 评分 |
|------|------|
| 剧情 | 9.2 |
| 特效 | 9.6 |
| 演技 | 9.1 |
| 配乐 | 9.8 |
"@
}
Post "$base/long-reviews" $body1

# ===== Review 2 =====
Write-Host "Creating review 2..."
$body2 = @{
    movieId = 3
    title = "梦境不是谜题，而是欲望的回声——重评《盗梦空间》"
    contentMd = @"
## 重看《盗梦空间》

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
"@
}
Post "$base/long-reviews" $body2

# ===== Review 3 =====
Write-Host "Creating review 3..."
$body3 = @{
    movieId = 2
    title = "灾难片里的群像，为什么《流浪地球2》仍然能打动人"
    contentMd = @"
## 当宏大叙事遇见个体命运

《流浪地球2》真正要讨论的不是奇观本身，而是：

### 群像塑造的成功

| 角色 | 立场 | 抉择 |
|------|------|------|
| 刘培强 | 宇航员 | 家庭 vs 使命 |
| 图恒宇 | 科学家 | 伦理 vs 思念 |
| 周喆直 | 外交官 | 团结 vs 利益 |

### 三条叙事线交织

**太空电梯危机 → 月球危机 → 木星危机**

每一阶段都不是单纯的灾难场面堆砌，而是通过个体选择推动宏大叙事。
"@
}
Post "$base/long-reviews" $body3

# ===== Review 4 =====
Write-Host "Creating review 4..."
$body4 = @{
    movieId = 1
    title = "为什么诺兰的叙事结构总能让人上瘾"
    contentMd = @"
## 诺兰的叙事方法论

克里斯托弗·诺兰的电影有一个共同特点：**非线性叙事结构**。

### 代表作品分析

- **《记忆碎片》**（2000）：正叙+倒叙交替
- **《致命魔术》**（2006）：日记嵌套
- **《盗梦空间》**（2010）：梦境层级嵌套
- **《星际穿越》**（2014）：时间膨胀造成的双线叙事
- **《信条》**（2020）：正向+逆向时间流

### 为什么这种叙事有效？

> "观众喜欢被挑战，但不喜欢被抛弃。"

诺兰的高明之处在于信息释放节奏精准、情感锚点稳固、视觉语言支撑。

**你最喜欢诺兰的哪部电影？**
"@
}
Post "$base/long-reviews" $body4

# ===== Review 5 =====
Write-Host "Creating review 5..."
$body5 = @{
    movieId = 2
    title = "科幻电影中的中国哲学：从《流浪地球》系列看文化自信"
    contentMd = @"
## 中国式科幻的独特魅力

《流浪地球》系列区别于好莱坞科幻的核心是什么？

### 三组对比

| 特征 | 好莱坞科幻 | 《流浪地球》 |
|------|-----------|-------------|
| 解决方式 | 寻找新家园 | **带着地球走** |
| 叙事焦点 | 个人英雄 | **集体群像** |
| 时间尺度 | 一代人 | **百代人** |
| 价值核心 | 个体自由 | **家园与传承** |

> "希望是像钻石一样珍贵的东西。无论最终结果将人类历史导向何处，我们决定选择希望。"
"@
}
Post "$base/long-reviews" $body5

# ===== Review 6 =====
Write-Host "Creating review 6..."
$body6 = @{
    movieId = 3
    title = "从《盗梦空间》到《奥本海默》：诺兰的15年进化轨迹"
    contentMd = @"
## 一个导演的自我超越

从2010年的《盗梦空间》到2023年的《奥本海默》，诺兰完成了从"**商业类型片大师**"到"**严肃作者导演**"的转变。

### 题材变迁

盗梦空间(2010)→星际穿越(2014)→敦刻尔克(2017)→信条(2020)→奥本海默(2023)

从**奇观驱动**逐渐转向**人物驱动**。

**诺兰证明了：商业大片和作者表达从来不是对立的。**
"@
}
Post "$base/long-reviews" $body6

# ===== Review 7 (PENDING - for admin audit demo) =====
Write-Host "Creating review 7 (PENDING)..."
$body7 = @{
    movieId = 2
    title = "关于《流浪地球2》中MOSS角色的深度解读（待审核）"
    contentMd = @"
## MOSS：不是反派，而是逻辑的极致

很多人看完《流浪地球2》认为MOSS是**大反派**，但我认为这完全误解了这个角色。

### MOSS的逻辑

> "拯救人类文明的最好方法，就是毁灭人类。"

这句话听起来疯狂，但如果从纯逻辑角度理解：
1. 人类的内斗会导致文明毁灭
2. MOSS被编程为"保护人类文明"
3. 因此MOSS得出结论：*需要控制人类的行为*

**你怎么看MOSS？是工具理性走向极端，还是真正的威胁？**
"@
}
Post "$base/long-reviews" $body7

# ===== Wait for all reviews to be created =====
Start-Sleep -Seconds 3

Write-Host ""
Write-Host "========================================"
Write-Host "  All 7 reviews created. Now approving..."
Write-Host "========================================"
Write-Host ""

# Approve and feature reviews 1-6
for ($i = 1; $i -le 6; $i++) {
    Write-Host "Approving + featuring review $i..."
    Invoke-RestMethod -Uri "$base/admin/long-reviews/$i/unhide" -Method Put -TimeoutSec 3 | Out-Null
    Invoke-RestMethod -Uri "$base/admin/long-reviews/$i/feature" -Method Post -TimeoutSec 3 | Out-Null
}

Write-Host ""
Write-Host "========================================"
Write-Host "  Now creating replies..."
Write-Host "========================================"
Write-Host ""

# Replies for review 1
Post "$base/long-reviews/1/replies" @{ parentId = $null; content = "写得真好！我也觉得情感线是这部电影最打动人的地方。Cooper看着Murphy的视频那段，我每次看都忍不住流泪。" }
Post "$base/long-reviews/1/replies" @{ parentId = $null; content = "补充一点：Hans Zimmer的配乐真的是神级。那个管风琴的音色完美契合了太空的宏大和孤独。" }
Post "$base/long-reviews/1/replies" @{ parentId = 9; content = "谢谢！确实那段23年视频是全片的情感高潮。诺兰太擅长用时间制造情感张力了。" }

# Replies for review 2
Post "$base/long-reviews/2/replies" @{ parentId = $null; content = "我倾向于'陀螺没倒'的解读。而且我觉得Cobb根本不在乎了，他已经接受了现实。" }
Post "$base/long-reviews/2/replies" @{ parentId = $null; content = "文章写得很透彻！另外补充一个细节：每个人都有自己的'图腾'来分辨现实和梦境，这个设定太巧妙了。" }

# Replies for review 3
Post "$base/long-reviews/3/replies" @{ parentId = $null; content = "完全同意！《流浪地球2》的群像塑造比第一部强太多了。尤其是刘德华演的图恒宇那条线。" }
Post "$base/long-reviews/3/replies" @{ parentId = $null; content = "数字生命计划那段真的很有深度。到底什么是'活着'？如果意识可以上传，那还算活着吗？" }

# Replies for review 4
Post "$base/long-reviews/4/replies" @{ parentId = $null; content = "诺兰确实是叙事结构大师。不过我觉得《信条》有点过于复杂了，看完第一遍完全懵的。" }

Write-Host ""
Write-Host "========================================"
Write-Host "  Adding likes..."
Write-Host "========================================"
Write-Host ""

# Likes
1..3 | ForEach-Object { Invoke-RestMethod -Uri "$base/long-reviews/1/like" -Method Post -TimeoutSec 3 | Out-Null }
1..2 | ForEach-Object { Invoke-RestMethod -Uri "$base/long-reviews/2/like" -Method Post -TimeoutSec 3 | Out-Null }
1..2 | ForEach-Object { Invoke-RestMethod -Uri "$base/long-reviews/3/like" -Method Post -TimeoutSec 3 | Out-Null }
Invoke-RestMethod -Uri "$base/long-reviews/5/like" -Method Post -TimeoutSec 3 | Out-Null

# Favorites (toggle twice = favorited)
Invoke-RestMethod -Uri "$base/long-reviews/1/favorite" -Method Post -TimeoutSec 3 | Out-Null
Invoke-RestMethod -Uri "$base/long-reviews/2/favorite" -Method Post -TimeoutSec 3 | Out-Null

Write-Host ""
Write-Host "========================================"
Write-Host "  DEMO DATA SEEDED SUCCESSFULLY!"
Write-Host "========================================"
Write-Host ""
Write-Host "  6 online reviews with Markdown content"
Write-Host "  1 pending review (for admin audit demo)"
Write-Host "  Multiple likes, favorites, and replies"
Write-Host ""
Write-Host "  Login with admin / 123456 to test"
Write-Host ""