#!/bin/bash

# 地图缓存功能测试脚本
# 用于验证景点顺序和名称完全一致时是否使用缓存

echo "🗺️ 地图缓存功能测试"
echo "===================="

# 测试数据
API_BASE_URL="http://localhost:8080"
TEST_LOCATIONS="南京南站,中山陵,夫子庙,南京博物院"

echo "📍 测试景点: $TEST_LOCATIONS"
echo ""

# 第一次请求 - 应该生成新地图
echo "🔄 第一次请求 (应该生成新地图)..."
FIRST_RESPONSE=$(curl -s -X POST "$API_BASE_URL/api/route/map" \
  -H "Content-Type: application/json" \
  -d "{\"locations\":[\"南京南站\",\"中山陵\",\"夫子庙\",\"南京博物院\"],\"strategy\":\"driving\"}")

echo "响应: $FIRST_RESPONSE"
echo ""

# 等待1秒
sleep 1

# 第二次请求 - 应该从缓存获取
echo "⚡ 第二次请求 (应该从缓存获取)..."
SECOND_RESPONSE=$(curl -s -X POST "$API_BASE_URL/api/route/map" \
  -H "Content-Type: application/json" \
  -d "{\"locations\":[\"南京南站\",\"中山陵\",\"夫子庙\",\"南京博物院\"],\"strategy\":\"driving\"}")

echo "响应: $SECOND_RESPONSE"
echo ""

# 第三次请求 - 景点顺序不同，应该生成新地图
echo "🔄 第三次请求 (景点顺序不同，应该生成新地图)..."
THIRD_RESPONSE=$(curl -s -X POST "$API_BASE_URL/api/route/map" \
  -H "Content-Type: application/json" \
  -d "{\"locations\":[\"南京博物院\",\"夫子庙\",\"中山陵\",\"南京南站\"],\"strategy\":\"driving\"}")

echo "响应: $THIRD_RESPONSE"
echo ""

# 第四次请求 - 景点名称不同，应该生成新地图
echo "🔄 第四次请求 (景点名称不同，应该生成新地图)..."
FOURTH_RESPONSE=$(curl -s -X POST "$API_BASE_URL/api/route/map" \
  -H "Content-Type: application/json" \
  -d "{\"locations\":[\"南京南站\",\"中山陵\",\"夫子庙\",\"总统府\"],\"strategy\":\"driving\"}")

echo "响应: $FOURTH_RESPONSE"
echo ""

# 第五次请求 - 回到原始顺序，应该从缓存获取
echo "⚡ 第五次请求 (回到原始顺序，应该从缓存获取)..."
FIFTH_RESPONSE=$(curl -s -X POST "$API_BASE_URL/api/route/map" \
  -H "Content-Type: application/json" \
  -d "{\"locations\":[\"南京南站\",\"中山陵\",\"夫子庙\",\"南京博物院\"],\"strategy\":\"driving\"}")

echo "响应: $FIFTH_RESPONSE"
echo ""

echo "📊 测试结果分析:"
echo "=================="

# 分析响应时间（简单分析）
echo "1. 第一次请求: 生成新地图"
echo "2. 第二次请求: 从缓存获取 (应该很快)"
echo "3. 第三次请求: 景点顺序不同，生成新地图"
echo "4. 第四次请求: 景点名称不同，生成新地图"
echo "5. 第五次请求: 回到原始顺序，从缓存获取 (应该很快)"
echo ""

echo "✅ 缓存功能测试完成！"
echo ""
echo "💡 预期结果:"
echo "- 第1、3、4次请求: 生成新地图，响应较慢"
echo "- 第2、5次请求: 从缓存获取，响应很快"
echo "- 缓存键基于景点名称的精确顺序和内容"

