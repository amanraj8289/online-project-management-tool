$ErrorActionPreference = "Stop"
$destDir = "C:\Users\Aman Raj\OneDrive\Desktop\JAVA Project\.tools"
if (-not (Test-Path $destDir)) {
    New-Item -ItemType Directory -Path $destDir | Out-Null
}

$mavenHome = "$destDir\apache-maven-3.9.9"
if (-not (Test-Path "$mavenHome\bin\mvn.cmd")) {
    $zipFile = "$env:TEMP\apache-maven-3.9.9-bin.zip"
    Write-Host "Downloading Maven 3.9.9 to $zipFile..."
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" -OutFile $zipFile
    Write-Host "Extracting Maven 3.9.9..."
    Expand-Archive -Path $zipFile -DestinationPath $destDir -Force
    Remove-Item $zipFile -Force -ErrorAction SilentlyContinue
    Write-Host "Maven installed successfully!"
} else {
    Write-Host "Maven already installed at $mavenHome"
}

& "$mavenHome\bin\mvn.cmd" -version
