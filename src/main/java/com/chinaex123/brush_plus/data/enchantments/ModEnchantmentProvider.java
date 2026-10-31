package com.chinaex123.brush_plus.data.enchantments;

import com.chinaex123.brush_plus.init.BPEnchantments;
import com.chinaex123.brush_plus.init.BPItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantmentProvider {

    private ModEnchantmentProvider() {}

    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder().add(Registries.ENCHANTMENT, ModEnchantmentProvider::bootstrap);

    private static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderSet<Item> brushes = items.getOrThrow(BPItemTags.BRUSHES);

        register(context, BPEnchantments.EXTRA_YIELD, BPEnchantmentBuilders.extraYield(brushes));
        register(context, BPEnchantments.GOLDEN_TOUCH, BPEnchantmentBuilders.goldenTouch(brushes));
        register(context, BPEnchantments.QUICK_BRUSH, BPEnchantmentBuilders.quickBrush(brushes));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

}