# 测试健康检查API
$uri = "http://localhost:8080/api/api/travel/itinerary/health"

Write-Host "测试健康检查API..."
Write-Host "URL: $uri"

try {
    $response = Invoke-RestMethod -Uri $uri -Method GET
    Write-Host "成功响应:"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody"
    }
}


