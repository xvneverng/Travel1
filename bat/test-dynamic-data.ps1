# 测试动态数据加载功能
Write-Host "测试动态数据加载功能..."

# 测试API端点
$uri = "http://localhost:8080/api/travel/itinerary/1"
Write-Host "`n1. 测试获取行程数据: $uri"

try {
    $response = Invoke-RestMethod -Uri $uri -Method GET -ContentType "application/json"
    Write-Host "✅ API响应成功:"
    Write-Host "   状态码: $($response.code)"
    Write-Host "   消息: $($response.message)"
    if ($response.data) {
        Write-Host "   数据ID: $($response.data.id)"
        Write-Host "   用户ID: $($response.data.userId)"
        Write-Host "   旅行日期: $($response.data.travelDate)"
        Write-Host "   天气: $($response.data.weatherCondition)"
        Write-Host "   温度: $($response.data.temperatureMin)-$($response.data.temperatureMax)°C"
        Write-Host "   注意事项: $($response.data.notes)"
        Write-Host "   景点数量: $($response.data.attractions.Count)"
    }
} catch {
    Write-Host "❌ API调用失败: $($_.Exception.Message)"
}

# 测试保存感受API
$uri2 = "http://localhost:8080/api/travel/itinerary/feelings"
$body2 = @{
    userId = "test_user"
    dayNumber = 1
    content = "测试动态数据更新功能"
} | ConvertTo-Json

Write-Host "`n2. 测试保存旅游感受: $uri2"
try {
    $response2 = Invoke-RestMethod -Uri $uri2 -Method POST -Body $body2 -ContentType "application/json"
    Write-Host "✅ 保存感受成功:"
    Write-Host "   状态码: $($response2.code)"
    Write-Host "   消息: $($response2.message)"
} catch {
    Write-Host "❌ 保存感受失败: $($_.Exception.Message)"
}

Write-Host "`n3. 测试完成！"
Write-Host "现在可以打开 travel-day1.html 查看动态数据加载效果"
