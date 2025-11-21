# 地图缓存功能测试脚本 (PowerShell版本)
# 用于验证景点顺序和名称完全一致时是否使用缓存

Write-Host "🗺️ 地图缓存功能测试" -ForegroundColor Cyan
Write-Host "====================" -ForegroundColor Cyan

# 测试数据
$API_BASE_URL = "http://localhost:8080"
$TEST_LOCATIONS = "南京南站,中山陵,夫子庙,南京博物院"

Write-Host "📍 测试景点: $TEST_LOCATIONS" -ForegroundColor Yellow
Write-Host ""

# 第一次请求 - 应该生成新地图
Write-Host "🔄 第一次请求 (应该生成新地图)..." -ForegroundColor Green
$firstRequest = @{
    locations = @("南京南站", "中山陵", "夫子庙", "南京博物院")
    strategy = "driving"
} | ConvertTo-Json

$FIRST_RESPONSE = Invoke-RestMethod -Uri "$API_BASE_URL/api/route/map" -Method POST -Body $firstRequest -ContentType "application/json"
Write-Host "响应: $($FIRST_RESPONSE | ConvertTo-Json -Depth 3)" -ForegroundColor White
Write-Host ""

# 等待1秒
Start-Sleep -Seconds 1

# 第二次请求 - 应该从缓存获取
Write-Host "⚡ 第二次请求 (应该从缓存获取)..." -ForegroundColor Green
$SECOND_RESPONSE = Invoke-RestMethod -Uri "$API_BASE_URL/api/route/map" -Method POST -Body $firstRequest -ContentType "application/json"
Write-Host "响应: $($SECOND_RESPONSE | ConvertTo-Json -Depth 3)" -ForegroundColor White
Write-Host ""

# 第三次请求 - 景点顺序不同，应该生成新地图
Write-Host "🔄 第三次请求 (景点顺序不同，应该生成新地图)..." -ForegroundColor Green
$thirdRequest = @{
    locations = @("南京博物院", "夫子庙", "中山陵", "南京南站")
    strategy = "driving"
} | ConvertTo-Json

$THIRD_RESPONSE = Invoke-RestMethod -Uri "$API_BASE_URL/api/route/map" -Method POST -Body $thirdRequest -ContentType "application/json"
Write-Host "响应: $($THIRD_RESPONSE | ConvertTo-Json -Depth 3)" -ForegroundColor White
Write-Host ""

# 第四次请求 - 景点名称不同，应该生成新地图
Write-Host "🔄 第四次请求 (景点名称不同，应该生成新地图)..." -ForegroundColor Green
$fourthRequest = @{
    locations = @("南京南站", "中山陵", "夫子庙", "总统府")
    strategy = "driving"
} | ConvertTo-Json

$FOURTH_RESPONSE = Invoke-RestMethod -Uri "$API_BASE_URL/api/route/map" -Method POST -Body $fourthRequest -ContentType "application/json"
Write-Host "响应: $($FOURTH_RESPONSE | ConvertTo-Json -Depth 3)" -ForegroundColor White
Write-Host ""

# 第五次请求 - 回到原始顺序，应该从缓存获取
Write-Host "⚡ 第五次请求 (回到原始顺序，应该从缓存获取)..." -ForegroundColor Green
$FIFTH_RESPONSE = Invoke-RestMethod -Uri "$API_BASE_URL/api/route/map" -Method POST -Body $firstRequest -ContentType "application/json"
Write-Host "响应: $($FIFTH_RESPONSE | ConvertTo-Json -Depth 3)" -ForegroundColor White
Write-Host ""

Write-Host "📊 测试结果分析:" -ForegroundColor Cyan
Write-Host "==================" -ForegroundColor Cyan

Write-Host "1. 第一次请求: 生成新地图" -ForegroundColor White
Write-Host "2. 第二次请求: 从缓存获取 (应该很快)" -ForegroundColor White
Write-Host "3. 第三次请求: 景点顺序不同，生成新地图" -ForegroundColor White
Write-Host "4. 第四次请求: 景点名称不同，生成新地图" -ForegroundColor White
Write-Host "5. 第五次请求: 回到原始顺序，从缓存获取 (应该很快)" -ForegroundColor White
Write-Host ""

Write-Host "✅ 缓存功能测试完成！" -ForegroundColor Green
Write-Host ""
Write-Host "💡 预期结果:" -ForegroundColor Yellow
Write-Host "- 第1、3、4次请求: 生成新地图，响应较慢" -ForegroundColor White
Write-Host "- 第2、5次请求: 从缓存获取，响应很快" -ForegroundColor White
Write-Host "- 缓存键基于景点名称的精确顺序和内容" -ForegroundColor White

