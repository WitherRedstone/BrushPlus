package com.chinaex123.brush_plus.event;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.init.BPCreativeTabs;
import com.chinaex123.brush_plus.init.BPEnchantments;
import com.chinaex123.brush_plus.init.BPItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = BrushPlus.MODID)
public class BPCreativeTabEvents {

    @SubscribeEvent
    public static void onBuildTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(BPCreativeTabs.BRUSH_PLUS.getKey())) {
            return;
        }

        HolderLookup.RegistryLookup<Enchantment> lookup = event.getParameters().holders().lookupOrThrow(Registries.ENCHANTMENT);
        ItemStack anchor = new ItemStack(BPItems.NETHERITE_BRUSH.get());

        addAllLevelEnchantmentBooksAfter(event, lookup, BPEnchantments.EXTRA_YIELD, anchor);
        addAllLevelEnchantmentBooksAfter(event, lookup, BPEnchantments.GOLDEN_TOUCH, anchor);
        addAllLevelEnchantmentBooksAfter(event, lookup, BPEnchantments.QUICK_BRUSH, anchor);
    }

    private static void addAllLevelEnchantmentBooksAfter(
            BuildCreativeModeTabContentsEvent event,
            HolderLookup.RegistryLookup<Enchantment> lookup,
            ResourceKey<Enchantment> key,
            ItemStack anchor) {
        lookup.get(key).ifPresent(holder -> {
            int maxLevel = holder.value().getMaxLevel();
            for (int level = 1; level <= maxLevel; level++) {
                ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, level));
                event.insertAfter(anchor, book, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        });
    }
}