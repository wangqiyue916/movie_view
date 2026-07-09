$projectDir = Split-Path -Parent $PSScriptRoot
Set-Location $projectDir
$out = @()
$out += "=== BRANCH ==="
$out += (git branch --show-current 2>&1)
$out += ""
$out += "=== BRANCHES ==="
$out += (git branch -a 2>&1)
$out += ""
$out += "=== REMOTE ==="
$out += (git remote -v 2>&1)
$out += ""
$out += "=== LOG (last 5) ==="
$out += (git log --oneline -5 2>&1)
$out += ""
$out += "=== STATUS ==="
$out += (git status --short 2>&1)
$out += ""
$out += "=== USER ==="
$out += "name: $(git config user.name 2>&1)"
$out += "email: $(git config user.email 2>&1)"
$out += ""
$out += "=== DIFF STAT ==="
$out += (git diff --stat HEAD 2>&1)

$out | Out-File -FilePath (Join-Path $projectDir "git_status.txt") -Encoding UTF8
Write-Host "DONE"