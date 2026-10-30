package com.chinaex123.brush_plus.compat.kubejs;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.compat.kubejs.recipe.BlockTransmutationRecipeJS;
import com.chinaex123.brush_plus.compat.kubejs.recipe.EntityHarvestingRecipeJS;
import com.chinaex123.brush_plus.init.BPRecipes;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;

public class ModKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        registry.namespace(BrushPlus.MODID).register(BPRecipes.BLOCK_TRANSMUTATION_SERIALIZER.getId().getPath(), BlockTransmutationRecipeJS.SCHEMA);
        registry.namespace(BrushPlus.MODID).register(BPRecipes.ENTITY_HARVESTING_SERIALIZER.getId().getPath(), EntityHarvestingRecipeJS.SCHEMA);
    }

}
