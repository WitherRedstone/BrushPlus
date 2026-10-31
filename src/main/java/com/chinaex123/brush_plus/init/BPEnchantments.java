package com.chinaex123.brush_plus.init;

import com.chinaex123.brush_plus.BrushPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class BPEnchantments {
    /** 额外馈赠 */
    public static final ResourceKey<Enchantment> EXTRA_YIELD = key("extra_yield");
    /** 刷石成金 */
    public static final ResourceKey<Enchantment> GOLDEN_TOUCH = key("golden_touch");
    /** 快刷手 */
    public static final ResourceKey<Enchantment> QUICK_BRUSH = key("quick_brush");

    public static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, BrushPlus.id(path));
    }
}