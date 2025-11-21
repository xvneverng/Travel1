# 测试控制器映射
Write-Host "测试控制器映射..."

# 测试根路径POST
$uri1 = "http://localhost:8080/api/travel/itinerary"
$body1 = @{
    userId = "test_user"
    dayNumber = 1
    travelDate = "2025-08-21"
    notes = "测试行程"
} | ConvertTo-Json

Write-Host "`n1. 测试根路径POST: $uri1"
try {
    $response1 = Invoke-RestMethod -Uri $uri1 -Method POST -Body $body1 -ContentType "application/json"
    Write-Host "成功: $($response1 | ConvertTo-Json -Depth 2)"
} catch {
    Write-Host "错误: $($_.Exception.Message)"
}

# 测试feelings路径POST
$uri2 = "http://localhost:8080/api/travel/itinerary/feelings"
$body2 = @{
    userId = "test_user"
    dayNumber = 1
    content = "测试感受"
} | ConvertTo-Json

Write-Host "`n2. 测试feelings路径POST: $uri2"
try {
    $response2 = Invoke-RestMethod -Uri $uri2 -Method POST -Body $body2 -ContentType "application/json"
    Write-Host "成功: $($response2 | ConvertTo-Json -Depth 2)"
} catch {
    Write-Host "错误: $($_.Exception.Message)"
}

# 测试健康检查
$uri3 = "http://localhost:8080/api/travel/itinerary/health"
Write-Host "`n3. 测试健康检查: $uri3"
try {
    $response3 = Invoke-RestMethod -Uri $uri3 -Method GET
    Write-Host "成功: $($response3 | ConvertTo-Json -Depth 2)"
} catch {
    Write-Host "错误: $($_.Exception.Message)"
}
