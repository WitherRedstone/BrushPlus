package com.chinaex123.brush_plus;

import com.chinaex123.brush_plus.config.BPClientConfig;
import com.chinaex123.brush_plus.init.BPCreativeTabs;
import com.chinaex123.brush_plus.init.BPEnchantments;
import com.chinaex123.brush_plus.init.BPItems;
import com.chinaex123.brush_plus.init.BPRecipes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(BrushPlus.MODID)
public class BrushPlus {
    public static final String MODID = "brush_plus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BrushPlus(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, BPClientConfig.SPEC);
        BPItems.ITEMS.register(modEventBus);
        BPRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        BPCreativeTabs.register(modEventBus);
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}