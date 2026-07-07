"""
长评模块 API 测试脚本
使用方法：
  1. 确保后端已启动（mvn spring-boot:run 或 java -jar target/*.jar）
  2. python test/api_test.py

测试覆盖：
  - 长评列表查询（分页、按电影、按热度排序）
  - 长评详情查询
  - 优质长评推荐接口
  - 创建长评（POST）
  - 修改长评（PUT）
  - 点赞（POST）
  - 收藏（POST）
  - 举报（POST）
  - 回复列表查询
  - 发布回复
  - 管理员设置/取消优质推荐
  - 管理员隐藏/恢复/删除长评
"""
import requests
import json
import sys

BASE_URL = "http://localhost:8080/api"

PASS = 0
FAIL = 0
DETAILS = []

def test(name, method, path, expected_status=200, data=None, params=None):
    global PASS, FAIL
    url = f"{BASE_URL}{path}"
    try:
        if method == "GET":
            resp = requests.get(url, params=params)
        elif method == "POST":
            resp = requests.post(url, json=data)
        elif method == "PUT":
            resp = requests.put(url, json=data)
        elif method == "DELETE":
            resp = requests.delete(url)
        else:
            DETAILS.append(f"  ✗ {name}: Unknown method {method}")
            FAIL += 1
            return

        status = resp.status_code
        body = resp.json() if resp.headers.get("content-type", "").startswith("application/json") else resp.text[:200]

        if status == expected_status:
            DETAILS.append(f"  ✓ {name} — HTTP {status}")
            PASS += 1
        else:
            DETAILS.append(f"  ✗ {name} — Expected {expected_status}, got {status}: {str(body)[:200]}")
            FAIL += 1
    except requests.ConnectionError:
        DETAILS.append(f"  ✗ {name} — Connection refused. Is the backend running?")
        FAIL += 1
    except Exception as e:
        DETAILS.append(f"  ✗ {name} — Error: {str(e)[:200]}")
        FAIL += 1


def run_tests():
    print("=" * 60)
    print("  长评模块 API 测试")
    print("  目标: " + BASE_URL)
    print("=" * 60)

    # ============ 1. 前台：长评列表 ============
    print("\n📋 1. 长评列表接口")
    test("查询全部ONLINE长评", "GET", "/long-reviews", params={"page": 1, "pageSize": 10})
    test("按热度排序", "GET", "/long-reviews", params={"sortBy": "hot", "page": 1, "pageSize": 5})
    test("按电影筛选（movieId=1）", "GET", "/long-reviews", params={"movieId": 1})
    test("分页第2页", "GET", "/long-reviews", params={"page": 2, "pageSize": 2})
    test("空页面（超出范围）", "GET", "/long-reviews", params={"page": 999, "pageSize": 10})

    # ============ 2. 前台：优质长评推荐 ============
    print("\n⭐ 2. 优质长评推荐接口")
    test("获取首页推荐长评", "GET", "/long-reviews/featured", params={"page": 1, "pageSize": 6})

    # ============ 3. 前台：长评详情 ============
    print("\n📖 3. 长评详情接口")
    test("查询存在的长评（id=1）", "GET", "/long-reviews/1")
    test("查询存在的长评（id=2）", "GET", "/long-reviews/2")
    test("查询不存在的长评", "GET", "/long-reviews/99999", expected_status=404)

    # ============ 4. 前台：创建长评 ============
    print("\n✏️ 4. 创建长评接口")
    test("正常创建", "POST", "/long-reviews", data={
        "movieId": 2,
        "title": "API测试：新建长评",
        "contentMd": "## 测试标题\n\n这是通过API创建的测试长评内容。\n\n- 列表项1\n- 列表项2",
        "coverUrl": "https://example.com/test_cover.jpg",
        "images": ["https://example.com/img1.jpg", "https://example.com/img2.jpg"]
    })
    test("缺少必填字段（movieId）", "POST", "/long-reviews", data={
        "title": "无电影的测试",
        "contentMd": "缺少movieId字段"
    }, expected_status=400)
    test("缺少必填字段（title）", "POST", "/long-reviews", data={
        "movieId": 1,
        "contentMd": "缺少title字段"
    }, expected_status=400)

    # ============ 5. 前台：修改长评 ============
    print("\n🔄 5. 修改长评接口")
    test("修改已驳回的长评（id=4是PENDING状态可修改）", "PUT", "/long-reviews/4", data={
        "title": "修改后的标题",
        "contentMd": "## 修改后的内容\n\n内容已被更新。"
    })

    # ============ 6. 前台：点赞 ============
    print("\n👍 6. 点赞接口")
    test("点赞长评（id=1）", "POST", "/long-reviews/1/like")
    test("重复点赞（toggle）", "POST", "/long-reviews/1/like")

    # ============ 7. 前台：收藏 ============
    print("\n⭐ 7. 收藏接口")
    test("收藏长评（id=3）", "POST", "/long-reviews/3/favorite")
    test("重复收藏（toggle）", "POST", "/long-reviews/3/favorite")

    # ============ 8. 前台：举报 ============
    print("\n🚩 8. 举报接口")
    test("举报长评", "POST", "/long-reviews/3/report", data={"reason": "内容包含不当言论"})

    # ============ 9. 前台：回复列表 ============
    print("\n💬 9. 回复列表接口")
    test("获取长评回复列表（id=1）", "GET", "/long-reviews/1/replies", params={"page": 1, "pageSize": 20})
    test("获取无回复的长评", "GET", "/long-reviews/3/replies", params={"page": 1, "pageSize": 20})

    # ============ 10. 前台：发布回复 ============
    print("\n📝 10. 发布回复接口")
    test("发布一级回复", "POST", "/long-reviews/1/replies", data={
        "content": "API测试：这是一条一级回复"
    })
    test("发布子回复", "POST", "/long-reviews/1/replies", data={
        "parentId": 1,
        "content": "API测试：这是对回复1的子回复"
    })
    test("回复内容为空", "POST", "/long-reviews/1/replies", data={
        "content": ""
    }, expected_status=400)
    test("回复内容超长（>1000字符）", "POST", "/long-reviews/1/replies", data={
        "content": "A" * 1001
    }, expected_status=400)

    # ============ 11. 管理端 ============
    print("\n🔧 11. 管理端接口")
    test("设为优质推荐（id=3）", "POST", "/admin/long-reviews/3/feature")
    test("取消优质推荐（id=3）", "DELETE", "/admin/long-reviews/3/feature")
    test("隐藏违规长评（id=3）", "PUT", "/admin/long-reviews/3/hide")
    test("恢复隐藏长评（id=3）", "PUT", "/admin/long-reviews/3/unhide")
    test("删除长评（id=999不存在的）", "DELETE", "/admin/long-reviews/99999", expected_status=404)

    # ============ 汇总 ============
    print("\n" + "=" * 60)
    print("  测试结果汇总")
    print("=" * 60)
    for d in DETAILS:
        print(d)
    total = PASS + FAIL
    print(f"\n  通过: {PASS}/{total} | 失败: {FAIL}/{total}")
    if FAIL == 0:
        print("  ✅ 所有测试通过!")
    else:
        print(f"  ⚠️  有 {FAIL} 个测试失败，请检查上面的详细信息。")
    print("=" * 60)

    return FAIL == 0


if __name__ == "__main__":
    success = run_tests()
    sys.exit(0 if success else 1)