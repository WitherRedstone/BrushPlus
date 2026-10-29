package com.chinaex123.brush_plus.data;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.init.BPItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BrushPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(BPItems.IRON_BRUSH.get());
        basicItem(BPItems.GOLD_BRUSH.get());
        basicItem(BPItems.DIAMOND_BRUSH.get());
        basicItem(BPItems.NETHERITE_BRUSH.get());
    }

}
