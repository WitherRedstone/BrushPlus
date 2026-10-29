package com.chinaex123.brush_plus.init;

import com.chinaex123.brush_plus.BrushPlus;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public interface BPItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(BrushPlus.MODID);

    /** 铁刷子 */
    DeferredItem<BrushItem> IRON_BRUSH = ITEMS.register("iron_brush", () -> new BrushItem(new Item.Properties().durability(128)));
    /** 金刷子 */
    DeferredItem<BrushItem> GOLD_BRUSH = ITEMS.register("gold_brush", () -> new BrushItem(new Item.Properties().durability(256)));
    /** 钻石刷子 */
    DeferredItem<BrushItem> DIAMOND_BRUSH = ITEMS.register("diamond_brush", () -> new BrushItem(new Item.Properties().durability(512)));
    /** 下界合金刷子 */
    DeferredItem<BrushItem> NETHERITE_BRUSH = ITEMS.register("netherite_brush", () -> new BrushItem(new Item.Properties().durability(1024).fireResistant()));

}