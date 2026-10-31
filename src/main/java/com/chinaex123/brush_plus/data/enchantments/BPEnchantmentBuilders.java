package com.chinaex123.brush_plus.data.enchantments;

import com.chinaex123.brush_plus.util.ColorEnchantment;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class BPEnchantmentBuilders {

    private BPEnchantmentBuilders() {}

    /** 额外馈赠：刷方块时有概率额外获得一份掉落物 */
    public static Enchantment.Builder extraYield(HolderSet<Item> brushes) {
        return colored(Enchantment.definition(
                brushes, brushes,
                5, 3,
                Enchantment.constantCost(8),
                Enchantment.dynamicCost(18, 8),
                4,
                EquipmentSlotGroup.MAINHAND
        ));
    }

    /** 刷石成金：刷完后有概率额外掉一个金粒 */
    public static Enchantment.Builder goldenTouch(HolderSet<Item> brushes) {
        return colored(Enchantment.definition(
                brushes, brushes,
                5, 3,
                Enchantment.constantCost(8),
                Enchantment.dynamicCost(18, 8),
                4,
                EquipmentSlotGroup.MAINHAND
        ));
    }

    /** 快刷手：提升刷子刷取的速度 */
    public static Enchantment.Builder quickBrush(HolderSet<Item> brushes) {
        return colored(Enchantment.definition(
                brushes, brushes,
                8, 3,
                Enchantment.constantCost(9),
                Enchantment.dynamicCost(15, 8),
                4,
                EquipmentSlotGroup.MAINHAND
        ));
    }

    private static Enchantment.Builder colored(Enchantment.EnchantmentDefinition definition) {
        return Enchantment.enchantment(definition).withCustomName(name -> name.withStyle(style -> style.withColor(ColorEnchantment.COLOR_PURPLE)));
    }
}