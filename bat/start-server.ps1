# PowerShell脚本启动本地HTTP服务器
Write-Host "启动本地HTTP服务器..." -ForegroundColor Green
Write-Host "服务器地址: http://localhost:8000" -ForegroundColor Yellow
Write-Host "按 Ctrl+C 停止服务器" -ForegroundColor Red
Write-Host ""

# 检查Python是否安装
try {
    python --version
    Write-Host "使用Python启动HTTP服务器..." -ForegroundColor Green
    python -m http.server 8000
} catch {
    Write-Host "Python未安装，尝试使用PowerShell启动..." -ForegroundColor Yellow
    # 使用PowerShell的简单HTTP服务器
    $listener = New-Object System.Net.HttpListener
    $listener.Prefixes.Add("http://localhost:8000/")
    $listener.Start()
    Write-Host "HTTP服务器已启动在 http://localhost:8000" -ForegroundColor Green
    
    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response
        
        $localPath = $request.Url.LocalPath
        if ($localPath -eq "/") { $localPath = "/travel-day1.html" }
        
        $filePath = Join-Path $PWD $localPath.TrimStart('/')
        
        if (Test-Path $filePath) {
            $content = [System.IO.File]::ReadAllBytes($filePath)
            $response.ContentLength64 = $content.Length
            $response.OutputStream.Write($content, 0, $content.Length)
        } else {
            $response.StatusCode = 404
            $errorMsg = [System.Text.Encoding]::UTF8.GetBytes("File not found")
            $response.OutputStream.Write($errorMsg, 0, $errorMsg.Length)
        }
        
        $response.Close()
    }
}
