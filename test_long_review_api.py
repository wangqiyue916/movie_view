"""
长评与审核模块 API 功能测试脚本
模拟前端使用者视角，测试完整的长评审核流程
"""
import urllib.request
import urllib.error
import json
import sys

BASE_URL = "http://localhost:8080/api"
TOKEN = "dev-token"  # 开发模式使用固定 token
ADMIN_TOKEN = "dev-token"

PASS_COUNT = 0
FAIL_COUNT = 0
WARN_COUNT = 0

def test(name, method, path, body=None, token=None, expected_status=200):
    """Execute a single API test"""
    global PASS_COUNT, FAIL_COUNT, WARN_COUNT

    url = f"{BASE_URL}{path}"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"

    data = json.dumps(body).encode() if body else None

    try:
        req = urllib.request.Request(url, data=data, headers=headers, method=method)
        resp = urllib.request.urlopen(req)
        result = json.loads(resp.read().decode())
        code = result.get("code", -1)

        if code == 0:
            print(f"  [PASS] {name}")
            print(f"         Response: {json.dumps(result, ensure_ascii=False)[:300]}")
            PASS_COUNT += 1
            return result
        else:
            print(f"  [WARN] {name} - code={code}, message={result.get('message')}")
            print(f"         Response: {json.dumps(result, ensure_ascii=False)[:300]}")
            WARN_COUNT += 1
            return result
    except urllib.error.HTTPError as e:
        body = e.read().decode()
        print(f"  [WARN] {name} - HTTP {e.code}")
        print(f"         Response: {body[:300]}")
        WARN_COUNT += 1
        try:
            return json.loads(body)
        except:
            return None
    except Exception as e:
        print(f"  [FAIL] {name} - ERROR: {e}")
        FAIL_COUNT += 1
        return None


def main():
    global PASS_COUNT, FAIL_COUNT, WARN_COUNT

    print("=" * 60)
    print("  Long Review & Audit Module API Test")
    print("=" * 60)
    print(f"  Base URL: {BASE_URL}")
    print(f"  Token: {TOKEN}")
    print()

    review_id = None

    # ============================================
    # Step 1: User Login
    # ============================================
    print("[Step 1] User Login (user001)")
    login_data = {"username": "user001", "password": "123456"}
    r = test("User Login", "POST", "/auth/login", body=login_data)
    if r and r.get("code") == 0:
        token_value = r["data"]["token"]
        print(f"         Token: {token_value}")
    print()

    # ============================================
    # Step 2: Admin Login
    # ============================================
    print("[Step 2] Admin Login (admin)")
    admin_data = {"username": "admin", "password": "123456"}
    r = test("Admin Login", "POST", "/auth/login", body=admin_data)
    print()

    # ============================================
    # Step 3: Get Long Review List
    # ============================================
    print("[Step 3] Get Long Review List (without token - public)")
    r = test("Public Review List", "GET", "/long-reviews?page=1&pageSize=10")
    if r and r.get("code") == 0:
        data_list = r["data"].get("list", [])
        print(f"         Total reviews: {r['data'].get('total', 0)}")
        for item in data_list[:3]:
            print(f"         - ID:{item.get('id')}, Title:{item.get('title','?')[:40]}, Status:{item.get('status','?')}")
    print()

    # ============================================
    # Step 4: Create Long Review
    # ============================================
    print("[Step 4] Create Long Review")
    create_body = {
        "movieId": 1,
        "title": "Test Review - Deep Analysis of Interstellar",
        "contentMd": "## Introduction\n\nThis film is a masterpiece of modern sci-fi.\n\n## Analysis\n\nThe narrative blends science and emotion perfectly.\n\n## Conclusion\n\nA must-watch for any film enthusiast.",
        "coverUrl": "https://example.com/test-cover.jpg"
    }
    r = test("Create Review", "POST", "/long-reviews", body=create_body, token=TOKEN)
    if r and r.get("code") == 0:
        review_id = r["data"].get("id")
        print(f"         Review ID created: {review_id}")
    print()

    # ============================================
    # Step 5: Get Long Review Detail (check status)
    # ============================================
    if review_id:
        print(f"[Step 5] Get Review Detail (ID={review_id})")
        r = test("View Review Detail", "GET", f"/long-reviews/{review_id}", token=TOKEN)
        if r and r.get("code") == 0:
            data = r["data"]
            print(f"         Title: {data.get('title','?')[:50]}")
            print(f"         Status: {data.get('status','?')}")
            print(f"         Views: {data.get('viewCount','?')}")
            print(f"         Likes: {data.get('likeCount','?')}")
            print(f"         Favorites: {data.get('favoriteCount','?')}")
            print(f"         Replies: {data.get('replyCount','?')}")
        print()

        # ============================================
        # Step 6: Like the Review
        # ============================================
        print(f"[Step 6] Like Review (ID={review_id})")
        test("Like Review", "POST", f"/long-reviews/{review_id}/like", token=TOKEN)
        print()

        # ============================================
        # Step 7: Favorite the Review
        # ============================================
        print(f"[Step 7] Favorite Review (ID={review_id})")
        test("Favorite Review", "POST", f"/long-reviews/{review_id}/favorite", token=TOKEN)
        print()

        # ============================================
        # Step 8: Post Reply to Review
        # ============================================
        print(f"[Step 8] Post Reply to Review (ID={review_id})")
        reply_body = {
            "content": "Great analysis! I totally agree with your perspective on the narrative structure."
        }
        r = test("Post Reply", "POST", f"/long-reviews/{review_id}/replies", body=reply_body, token=ADMIN_TOKEN)
        if r and r.get("code") == 0:
            reply_id = r["data"].get("id")
            print(f"         Reply ID: {reply_id}")
        print()

        # ============================================
        # Step 9: Get Replies List
        # ============================================
        print(f"[Step 9] Get Replies for Review (ID={review_id})")
        r = test("Get Replies", "GET", f"/long-reviews/{review_id}/replies?page=1&pageSize=10")
        if r and r.get("code") == 0:
            replies = r["data"].get("list", [])
            print(f"         Total replies: {r['data'].get('total', 0)}")
            for rep in replies:
                print(f"         - ID:{rep.get('id')}, Content:{rep.get('content','')[:50]}")
        print()

        # ============================================
        # Step 10: Admin Set Featured
        # ============================================
        print(f"[Step 10] Admin Set Featured (ID={review_id})")
        test("Set Featured", "POST", f"/admin/long-reviews/{review_id}/feature", token=ADMIN_TOKEN)
        print()

        # ============================================
        # Step 11: Verify Featured Status
        # ============================================
        print(f"[Step 11] Verify Featured Status (ID={review_id})")
        r = test("Verify Detail", "GET", f"/long-reviews/{review_id}", token=TOKEN)
        if r and r.get("code") == 0:
            data = r["data"]
            print(f"         Is Featured: {data.get('featured','?')}")
            print(f"         Status: {data.get('status','?')}")
        print()

        # ============================================
        # Step 12: Report Review
        # ============================================
        print(f"[Step 12] Report Review (ID={review_id})")
        report_body = {"reason": "Content appears to be duplicated from external sources"}
        test("Report Review", "POST", f"/long-reviews/{review_id}/report", body=report_body, token=TOKEN)
        print()

        # ============================================
        # Step 13: Admin Hide Review
        # ============================================
        print(f"[Step 13] Admin Hide Review (ID={review_id})")
        test("Hide Review", "PUT", f"/admin/long-reviews/{review_id}/hide", token=ADMIN_TOKEN)
        print()

        # ============================================
        # Step 14: Admin Unhide Review
        # ============================================
        print(f"[Step 14] Admin Unhide Review (ID={review_id})")
        test("Unhide Review", "PUT", f"/admin/long-reviews/{review_id}/unhide", token=ADMIN_TOKEN)
        print()

        # ============================================
        # Step 15: Update Review
        # ============================================
        print(f"[Step 15] Update Review (ID={review_id})")
        update_body = {
            "title": "Test Review (Revised) - Enhanced Analysis",
            "contentMd": "## Updated Review\n\nAfter re-watching, I have more insights to share.\n\n## New Perspective\n\nThe film's use of practical effects adds authenticity.",
            "coverUrl": "https://example.com/revised-cover.jpg"
        }
        test("Update Review", "PUT", f"/long-reviews/{review_id}", body=update_body, token=TOKEN)
        print()

        # ============================================
        # Step 16: Get My Reviews
        # ============================================
        print(f"[Step 16] Get My Reviews (user)")
        r = test("My Reviews", "GET", "/long-reviews/my?page=1&pageSize=10", token=TOKEN)
        if r and r.get("code") == 0:
            my_list = r["data"].get("list", [])
            print(f"         My reviews count: {r['data'].get('total', 0)}")
            for item in my_list[:3]:
                print(f"         - ID:{item.get('id')}, Title:{item.get('title','?')[:40]}, Status:{item.get('status','?')}")
        print()

        # ============================================
        # Step 17: Get Featured Reviews
        # ============================================
        print(f"[Step 17] Get Featured Reviews")
        r = test("Featured Reviews", "GET", "/long-reviews/featured?page=1&pageSize=10")
        if r and r.get("code") == 0:
            feat_list = r["data"].get("list", [])
            print(f"         Featured reviews: {r['data'].get('total', 0)}")
            for item in feat_list[:3]:
                print(f"         - ID:{item.get('id')}, Title:{item.get('title','?')[:40]}")
        print()

        # ============================================
        # Step 18: Admin Unset Featured
        # ============================================
        print(f"[Step 18] Admin Unset Featured (ID={review_id})")
        test("Unset Featured", "DELETE", f"/admin/long-reviews/{review_id}/feature", token=ADMIN_TOKEN)
        print()

        # ============================================
        # Step 19: Admin Delete Review (cleanup)
        # ============================================
        print(f"[Step 19] Admin Delete Review (cleanup) (ID={review_id})")
        test("Delete Review", "DELETE", f"/admin/long-reviews/{review_id}", token=ADMIN_TOKEN)
        print()

    # ============================================
    # Summary
    # ============================================
    total = PASS_COUNT + FAIL_COUNT + WARN_COUNT
    print("=" * 60)
    print(f"  Test Results: {PASS_COUNT} PASS, {WARN_COUNT} WARN, {FAIL_COUNT} FAIL")
    print(f"  Total: {total} tests")
    print("=" * 60)


if __name__ == "__main__":
    main()