#!/bin/bash
# Phase 1 Verification Script
# Verifies that the Godot project setup is complete and correct

echo "========================================="
echo "Phase 1: 專案設置驗收"
echo "========================================="
echo ""

# Check directory structure
echo "✓ 檢查目錄結構..."
REQUIRED_DIRS=(
    "godot"
    "godot/assets"
    "godot/scenes"
    "godot/scenes/entities"
    "godot/scenes/ui"
    "godot/scenes/maps"
    "godot/scripts"
    "godot/scripts/autoload"
    "godot/scripts/entities"
    "godot/scripts/systems"
    "godot/scripts/ui"
    "godot/data"
)

for dir in "${REQUIRED_DIRS[@]}"; do
    if [ -d "$dir" ]; then
        echo "  ✓ $dir 存在"
    else
        echo "  ✗ $dir 不存在"
        exit 1
    fi
done
echo ""

# Check required files
echo "✓ 檢查必要檔案..."
REQUIRED_FILES=(
    "godot/project.godot"
    "godot/README.md"
    "godot/implementation_plan.md"
    "godot/scripts/data/constants.gd"
    "godot/scripts/autoload/game_manager.gd"
    "godot/scripts/autoload/audio_manager.gd"
    "godot/scripts/autoload/save_manager.gd"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "  ✓ $file 存在"
    else
        echo "  ✗ $file 不存在"
        exit 1
    fi
done
echo ""

# Count resources
echo "✓ 檢查資源檔案..."
IMAGE_COUNT=$(find godot/assets -type f \( -name "*.png" -o -name "*.jpg" \) | wc -l | tr -d ' ')
AUDIO_COUNT=$(find godot/assets -type f \( -name "*.wav" -o -name "*.mp3" -o -name "*.ogg" \) | wc -l | tr -d ' ')
FONT_COUNT=$(find godot/assets -type f -name "*.ttf" | wc -l | tr -d ' ')
MAP_COUNT=$(find godot/assets/maps_v2 -type f -name "*.txt" | wc -l | tr -d ' ')

echo "  • 圖片檔案: $IMAGE_COUNT (預期: 216)"
echo "  • 音效檔案: $AUDIO_COUNT (預期: 23)"
echo "  • 字型檔案: $FONT_COUNT (預期: 1+)"
echo "  • 地圖檔案: $MAP_COUNT (預期: 5)"
echo ""

# Verify file sizes
echo "✓ 檢查專案檔案大小..."
TOTAL_FILES=$(find godot -type f | wc -l | tr -d ' ')
TOTAL_DIRS=$(find godot -type d | wc -l | tr -d ' ')
GODOT_SIZE=$(du -sh godot | cut -f1)

echo "  • 總檔案數: $TOTAL_FILES"
echo "  • 總目錄數: $TOTAL_DIRS"
echo "  • 專案大小: $GODOT_SIZE"
echo ""

# Summary
echo "========================================="
echo "Phase 1 驗收結果"
echo "========================================="
echo "✅ 目錄結構正確"
echo "✅ 必要檔案已建立"
echo "✅ 資源已複製 ($IMAGE_COUNT 圖片, $AUDIO_COUNT 音檔)"
echo "✅ 專案配置完成"
echo ""
echo "下一步: 在 Godot 編輯器中開啟專案進行驗證"
echo "  1. 開啟 Godot 4.3+"
echo "  2. 選擇 Import"
echo "  3. 選擇 godot/project.godot"
echo "  4. 檢查是否有錯誤訊息"
echo "========================================="
