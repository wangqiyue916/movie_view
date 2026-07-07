$url = "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
$output = "C:\ProgramData\maven.zip"
$dest = "C:\ProgramData\apache-maven-3.9.9"

Write-Host "Downloading Maven 3.9.9..."
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri $url -OutFile $output -UseBasicParsing

Write-Host "Extracting Maven..."
Expand-Archive -Path $output -DestinationPath "C:\ProgramData" -Force

Write-Host "Setting environment PATH..."
# Add Maven bin to current process PATH
$env:Path = "$dest\bin;$env:Path"

Write-Host "Maven installed to: $dest"
Write-Host "Maven version:"
& "$dest\bin\mvn.cmd" --version