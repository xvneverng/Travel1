# 测试经纬度缓存优化功能
# 测试脚本：验证地图生成性能优化

Write-Host "=== 经纬度缓存优化测试 ===" -ForegroundColor Green

# 测试景点列表
$testAttractions = @(
    "南京南站",
    "中山陵", 
    "夫子庙",
    "南京博物院"
)

Write-Host "`n1. 检查景点坐标缓存状态..." -ForegroundColor Yellow
$statusBody = $testAttractions | ConvertTo-Json
$statusResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/route/coordinates/status" -Method POST -Body $statusBody -ContentType "application/json"
Write-Host "缓存状态检查结果:" -ForegroundColor Cyan
Write-Host $statusResponse

Write-Host "`n2. 第一次生成地图（可能调用API）..." -ForegroundColor Yellow
$startTime = Get-Date
$routeRequest = @{
    locations = $testAttractions
    strategy = "driving"
} | ConvertTo-Json

$mapResponse1 = Invoke-RestMethod -Uri "http://localhost:8080/api/route/map" -Method POST -Body $routeRequest -ContentType "application/json"
$endTime = Get-Date
$duration1 = ($endTime - $startTime).TotalMilliseconds

Write-Host "第一次地图生成耗时: $([math]::Round($duration1, 2)) 毫秒" -ForegroundColor Cyan
Write-Host "地图URL: $($mapResponse1.data.mapUrl)" -ForegroundColor Cyan

Write-Host "`n3. 再次检查缓存状态..." -ForegroundColor Yellow
$statusResponse2 = Invoke-RestMethod -Uri "http://localhost:8080/api/route/coordinates/status" -Method POST -Body $statusBody -ContentType "application/json"
Write-Host "缓存状态检查结果:" -ForegroundColor Cyan
Write-Host $statusResponse2

Write-Host "`n4. 第二次生成地图（应该从缓存获取）..." -ForegroundColor Yellow
$startTime2 = Get-Date
$mapResponse2 = Invoke-RestMethod -Uri "http://localhost:8080/api/route/map" -Method POST -Body $routeRequest -ContentType "application/json"
$endTime2 = Get-Date
$duration2 = ($endTime2 - $startTime2).TotalMilliseconds

Write-Host "第二次地图生成耗时: $([math]::Round($duration2, 2)) 毫秒" -ForegroundColor Cyan
Write-Host "地图URL: $($mapResponse2.data.mapUrl)" -ForegroundColor Cyan

Write-Host "`n5. 性能对比分析..." -ForegroundColor Yellow
$improvement = if ($duration1 -gt 0) { [math]::Round((($duration1 - $duration2) / $duration1) * 100, 2) } else { 0 }
Write-Host "第一次耗时: $([math]::Round($duration1, 2)) 毫秒" -ForegroundColor White
Write-Host "第二次耗时: $([math]::Round($duration2, 2)) 毫秒" -ForegroundColor White
Write-Host "性能提升: $improvement%" -ForegroundColor $(if ($improvement -gt 0) { "Green" } else { "Red" })

Write-Host "`n6. 测试批量更新功能..." -ForegroundColor Yellow
$updateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/route/coordinates/update" -Method POST -Body $statusBody -ContentType "application/json"
Write-Host "批量更新结果:" -ForegroundColor Cyan
Write-Host $updateResponse

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "优化说明:" -ForegroundColor Yellow
Write-Host "- 第一次生成地图时会调用高德API获取经纬度并保存到数据库" -ForegroundColor White
Write-Host "- 后续生成地图时优先从数据库缓存获取经纬度，大幅提升性能" -ForegroundColor White
Write-Host "- 支持手动批量更新景点经纬度缓存" -ForegroundColor White
Write-Host "- 提供缓存状态检查功能" -ForegroundColor White
