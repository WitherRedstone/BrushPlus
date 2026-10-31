package com.chinaex123.brush_plus.init;

import com.chinaex123.brush_plus.BrushPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface BPItemTags {

    TagKey<Item> BRUSHES = bind("brushes");

    private static TagKey<Item> bind(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BrushPlus.MODID, name));
    }
}