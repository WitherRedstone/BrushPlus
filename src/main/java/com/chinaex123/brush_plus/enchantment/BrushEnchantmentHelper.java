package com.chinaex123.brush_plus.enchantment;

import com.chinaex123.brush_plus.init.BPEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class BrushEnchantmentHelper {

    private BrushEnchantmentHelper() {}

    public static int getExtraYieldLevel(ItemStack brush, Level level) {
        var lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return lookup.get(BPEnchantments.EXTRA_YIELD)
                .map(brush::getEnchantmentLevel)
                .orElse(0);
    }

    public static int rollExtraYield(ItemStack brush, Level level, int baseCount) {
        int extraYieldLevel = getExtraYieldLevel(brush, level);
        if (extraYieldLevel <= 0) return baseCount;

        float extraChance = extraYieldLevel * 0.30f;
        if (level.getRandom().nextFloat() < extraChance) {
            return baseCount + 1;
        }
        return baseCount;
    }

    public static int getGoldenTouchLevel(ItemStack brush, Level level) {
        var lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return lookup.get(BPEnchantments.GOLDEN_TOUCH)
                .map(brush::getEnchantmentLevel)
                .orElse(0);
    }

    public static void rollGoldenTouch(ItemStack brush, Level level, BlockPos pos) {
        int level_ = getGoldenTouchLevel(brush, level);
        if (level_ <= 0) return;

        if (level.getRandom().nextFloat() < level_ * 0.10f) {
            spawnDropItem(level, pos, new ItemStack(Items.GOLD_NUGGET));
        }
    }

    public static int getQuickBrushLevel(ItemStack brush, Level level) {
        var lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return lookup.get(BPEnchantments.QUICK_BRUSH)
                .map(brush::getEnchantmentLevel)
                .orElse(0);
    }

    public static int calcEffectiveTotalTicks(ItemStack brush, Level level, int baseTotalTicks) {
        int speedLevel = getQuickBrushLevel(brush, level);
        if (speedLevel <= 0) return baseTotalTicks;

        float speedMultiplier = 1.0f + speedLevel * 0.25f;
        return Math.max(1, Math.round(baseTotalTicks / speedMultiplier));
    }

    public static void spawnDropItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                stack
        );
        entity.setDeltaMovement(
                (level.getRandom().nextDouble() - 0.5) * 0.15,
                0.35,
                (level.getRandom().nextDouble() - 0.5) * 0.15
        );
        entity.setPickUpDelay(10);
        level.addFreshEntity(entity);
    }
}