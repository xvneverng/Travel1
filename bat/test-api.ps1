# 测试旅游感受保存API
$uri = "http://localhost:8080/api/api/travel/itinerary/feelings"
$body = @{
    userId = "default_user"
    dayNumber = 1
    content = "测试旅游感受内容"
} | ConvertTo-Json

Write-Host "测试保存旅游感受API..."
Write-Host "URL: $uri"
Write-Host "Body: $body"

try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Body $body -ContentType "application/json"
    Write-Host "成功响应:"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    Write-Host "响应内容: $($_.Exception.Response)"
}

Write-Host "`n测试获取旅游感受API..."
$getUri = "http://localhost:8080/api/api/travel/itinerary/feelings/1?userId=default_user"
try {
    $getResponse = Invoke-RestMethod -Uri $getUri -Method GET
    Write-Host "成功响应:"
    $getResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    Write-Host "响应内容: $($_.Exception.Response)"
}
