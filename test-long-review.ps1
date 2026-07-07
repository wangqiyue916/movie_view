$baseUrl = "http://localhost:8080/api"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Long Review & Audit Module API Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: User Login
Write-Host "[Step 1] User Login (user001)" -ForegroundColor Yellow
$loginBody = '{"username":"user001","password":"123456"}'
try {
    $loginResult = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
    $userToken = $loginResult.data.token
    Write-Host "  [OK] User login success" -ForegroundColor Green
} catch {
    Write-Host "  [FAIL] User login failed: $_" -ForegroundColor Red
    try {
        $errReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "  Response: $($errReader.ReadToEnd())" -ForegroundColor Red
        $errReader.Close()
    } catch {}
}
Write-Host ""

# Step 2: Admin Login
Write-Host "[Step 2] Admin Login (admin)" -ForegroundColor Yellow
$adminBody = '{"username":"admin","password":"123456"}'
try {
    $adminResult = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $adminBody
    $adminToken = $adminResult.data.token
    Write-Host "  [OK] Admin login success" -ForegroundColor Green
} catch {
    Write-Host "  [FAIL] Admin login failed: $_" -ForegroundColor Red
}
Write-Host ""

# Step 3: Get Movie List
Write-Host "[Step 3] Get Movie List" -ForegroundColor Yellow
$movieId = 1
try {
    $movieUrl = "$baseUrl/movies" + "?page=1" + "&pageSize=5"
    $moviesResult = Invoke-RestMethod -Uri $movieUrl -Method Get
    $movies = $moviesResult.data.list
    if ($movies -and $movies.Count -gt 0) {
        $movieId = $movies[0].id
        $movieName = $movies[0].name
        Write-Host "  [OK] Found $($movies.Count) movies, using first: ID=$movieId, Name=$movieName" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] No movies found, using default ID=1" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  [WARN] Get movies failed: $_" -ForegroundColor Yellow
}
Write-Host ""

# Step 4: Create Long Review
Write-Host "[Step 4] Create Long Review (by user)" -ForegroundColor Yellow
$createBody = @{
    movieId = $movieId
    title = "Test Review - A thought-provoking film"
    contentMd = "## Plot Analysis`n`nThe narrative structure is exquisite, with three timelines interwoven seamlessly.`n`n## Performance Review`n`nThe lead actor delivers an impressive performance.`n`n## Conclusion`n`nOverall, a well-crafted masterpiece worth revisiting."
    coverUrl = "https://example.com/test-cover.jpg"
} | ConvertTo-Json
$reviewId = $null
try {
    $createResult = Invoke-RestMethod -Uri "$baseUrl/long-reviews" -Method Post -ContentType "application/json" -Body $createBody -Headers @{Authorization="Bearer $userToken"}
    Write-Host "  [OK] Review created!" -ForegroundColor Green
    $reviewId = $createResult.data.id
    Write-Host "  Review ID: $reviewId" -ForegroundColor Green
    Write-Host "  Title: $($createResult.data.title)" -ForegroundColor Green
    Write-Host "  Status: $($createResult.data.status) (should be PENDING)" -ForegroundColor Green
} catch {
    Write-Host "  [FAIL] Create review failed: $($_.Exception.Message)" -ForegroundColor Red
    try {
        $errReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "  Response: $($errReader.ReadToEnd())" -ForegroundColor Red
        $errReader.Close()
    } catch {}
}
Write-Host ""

if ($reviewId) {
    # Step 5: View Review List
    Write-Host "[Step 5] View Review List" -ForegroundColor Yellow
    try {
        $listUrl = "$baseUrl/long-reviews" + "?movieId=$movieId" + "&page=1" + "&pageSize=10"
        $listResult = Invoke-RestMethod -Uri $listUrl -Method Get -Headers @{Authorization="Bearer $userToken"}
        Write-Host "  [OK] Review list: total=$($listResult.data.total)" -ForegroundColor Green
        foreach ($item in $listResult.data.list) {
            Write-Host "    - ID: $($item.id), Title: $($item.title), Status: $($item.status)" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  [WARN] List reviews failed: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 6: Admin Reject Review
    Write-Host "[Step 6] Admin Reject Review" -ForegroundColor Yellow
    $rejectBody = @{
        rejectReason = "Cover image URL is invalid, please provide a valid URL"
    } | ConvertTo-Json
    try {
        $rejectUrl = "$baseUrl/admin/long-reviews/$reviewId/reject"
        Invoke-RestMethod -Uri $rejectUrl -Method Post -ContentType "application/json" -Body $rejectBody -Headers @{Authorization="Bearer $adminToken"} | Out-Null
        Write-Host "  [OK] Review rejected" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Reject returned: $($_.Exception.Message)" -ForegroundColor Yellow
        try {
            $errReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "  Response: $($errReader.ReadToEnd())" -ForegroundColor Yellow
            $errReader.Close()
        } catch {}
    }
    Write-Host ""

    # Step 7: User Edit Rejected Review
    Write-Host "[Step 7] User Edit Rejected Review" -ForegroundColor Yellow
    $updateBody = @{
        title = "Test Review (Revised) - In-depth analysis"
        contentMd = "## Revised Content`n`nChanges made per moderator feedback. Cover link updated.`n`n## Theme Analysis`n`nThis film explores the relationship between humanity and technology."
        coverUrl = "https://example.com/updated-cover.jpg"
    } | ConvertTo-Json
    try {
        $updateUrl = "$baseUrl/long-reviews/$reviewId"
        $updateResult = Invoke-RestMethod -Uri $updateUrl -Method Put -ContentType "application/json" -Body $updateBody -Headers @{Authorization="Bearer $userToken"}
        Write-Host "  [OK] Review updated, status: $($updateResult.data.status) (should be PENDING)" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Edit review failed: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 8: Admin Approve Review
    Write-Host "[Step 8] Admin Approve Review" -ForegroundColor Yellow
    try {
        $approveUrl = "$baseUrl/admin/long-reviews/$reviewId/approve"
        Invoke-RestMethod -Uri $approveUrl -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $adminToken"} | Out-Null
        Write-Host "  [OK] Review approved!" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Approve returned: $($_.Exception.Message)" -ForegroundColor Yellow
        try {
            $errReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "  Response: $($errReader.ReadToEnd())" -ForegroundColor Yellow
            $errReader.Close()
        } catch {}
    }
    Write-Host ""

    # Step 9: View Review Detail
    Write-Host "[Step 9] View Review Detail" -ForegroundColor Yellow
    try {
        $detailUrl = "$baseUrl/long-reviews/$reviewId"
        $detailResult = Invoke-RestMethod -Uri $detailUrl -Method Get -Headers @{Authorization="Bearer $userToken"}
        Write-Host "  [OK] Review detail:" -ForegroundColor Green
        Write-Host "    ID: $($detailResult.data.id)" -ForegroundColor Green
        Write-Host "    Title: $($detailResult.data.title)" -ForegroundColor Green
        Write-Host "    Status: $($detailResult.data.status)" -ForegroundColor Green
        Write-Host "    Likes: $($detailResult.data.likeCount)" -ForegroundColor Green
        Write-Host "    Favorites: $($detailResult.data.favoriteCount)" -ForegroundColor Green
        Write-Host "    Views: $($detailResult.data.viewCount)" -ForegroundColor Green
        Write-Host "    Replies: $($detailResult.data.replyCount)" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Detail failed: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 10: Like Review
    Write-Host "[Step 10] Like Review" -ForegroundColor Yellow
    try {
        $likeUrl = "$baseUrl/long-reviews/$reviewId/like"
        Invoke-RestMethod -Uri $likeUrl -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $userToken"} | Out-Null
        Write-Host "  [OK] Liked" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Like returned: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 11: Favorite Review
    Write-Host "[Step 11] Favorite Review" -ForegroundColor Yellow
    try {
        $favUrl = "$baseUrl/long-reviews/$reviewId/favorite"
        Invoke-RestMethod -Uri $favUrl -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $userToken"} | Out-Null
        Write-Host "  [OK] Favorited" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Favorite returned: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 12: Post Reply
    Write-Host "[Step 12] Post Reply" -ForegroundColor Yellow
    $replyBody = @{
        parentId = $null
        content = "Great review! I completely agree with your analysis."
    } | ConvertTo-Json
    try {
        $replyUrl = "$baseUrl/long-reviews/$reviewId/replies"
        $replyResult = Invoke-RestMethod -Uri $replyUrl -Method Post -ContentType "application/json" -Body $replyBody -Headers @{Authorization="Bearer $adminToken"}
        Write-Host "  [OK] Reply posted, ID: $($replyResult.data.id)" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Post reply failed: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 13: Get Replies
    Write-Host "[Step 13] Get Replies" -ForegroundColor Yellow
    try {
        $repliesUrl = "$baseUrl/long-reviews/$reviewId/replies" + "?page=1" + "&pageSize=10"
        $repliesResult = Invoke-RestMethod -Uri $repliesUrl -Method Get
        Write-Host "  [OK] Replies list: total=$($repliesResult.data.total)" -ForegroundColor Green
        foreach ($r in $repliesResult.data.list) {
            Write-Host "    - ID: $($r.id), Content: $($r.content)" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  [WARN] Get replies failed: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 14: Set Featured Review
    Write-Host "[Step 14] Admin Set Featured Review" -ForegroundColor Yellow
    try {
        $featUrl = "$baseUrl/admin/long-reviews/$reviewId/feature"
        Invoke-RestMethod -Uri $featUrl -Method Post -ContentType "application/json" -Headers @{Authorization="Bearer $adminToken"} | Out-Null
        Write-Host "  [OK] Set as featured" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Feature returned: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 15: Query Audit Records
    Write-Host "[Step 15] Query Audit Records" -ForegroundColor Yellow
    try {
        $auditUrl = "$baseUrl/admin/audit-records" + "?targetType=LONG_REVIEW" + "&page=1" + "&pageSize=10"
        $auditResult = Invoke-RestMethod -Uri $auditUrl -Method Get -Headers @{Authorization="Bearer $adminToken"}
        Write-Host "  [OK] Audit records: total=$($auditResult.data.total)" -ForegroundColor Green
        foreach ($a in $auditResult.data.list) {
            Write-Host "    - AuditID: $($a.id), TargetID: $($a.targetId), Action: $($a.action), Status: $($a.afterStatus)" -ForegroundColor Gray
        }
    } catch {
        Write-Host "  [WARN] Audit records returned: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""

    # Step 16: Report Review
    Write-Host "[Step 16] Report Review" -ForegroundColor Yellow
    $reportBody = @{
        reason = "Suspected plagiarism in the review content"
    } | ConvertTo-Json
    try {
        $reportUrl = "$baseUrl/long-reviews/$reviewId/report"
        Invoke-RestMethod -Uri $reportUrl -Method Post -ContentType "application/json" -Body $reportBody -Headers @{Authorization="Bearer $adminToken"} | Out-Null
        Write-Host "  [OK] Report submitted" -ForegroundColor Green
    } catch {
        Write-Host "  [WARN] Report returned: $($_.Exception.Message)" -ForegroundColor Yellow
    }
    Write-Host ""
}

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Test Complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Test coverage:" -ForegroundColor White
Write-Host "  1.  User login" -ForegroundColor Gray
Write-Host "  2.  Admin login" -ForegroundColor Gray
Write-Host "  3.  Get movie list" -ForegroundColor Gray
Write-Host "  4.  Create long review (status PENDING)" -ForegroundColor Gray
Write-Host "  5.  Query review list" -ForegroundColor Gray
Write-Host "  6.  Admin reject review (with reason)" -ForegroundColor Gray
Write-Host "  7.  User edit rejected review (back to PENDING)" -ForegroundColor Gray
Write-Host "  8.  Admin approve review (APPROVED)" -ForegroundColor Gray
Write-Host "  9.  View review detail (verify status & stats)" -ForegroundColor Gray
Write-Host "  10. Like review" -ForegroundColor Gray
Write-Host "  11. Favorite review" -ForegroundColor Gray
Write-Host "  12. Post reply to review" -ForegroundColor Gray
Write-Host "  13. Get replies list" -ForegroundColor Gray
Write-Host "  14. Admin set featured review" -ForegroundColor Gray
Write-Host "  15. Query audit records" -ForegroundColor Gray
Write-Host "  16. Report review" -ForegroundColor Gray