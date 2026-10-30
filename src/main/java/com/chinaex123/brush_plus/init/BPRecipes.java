package com.chinaex123.brush_plus.init;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.crafting.recipe.BlockTransmutationRecipe;
import com.chinaex123.brush_plus.crafting.recipe.EntityHarvestingRecipe;
import com.chinaex123.brush_plus.crafting.serializer.BlockTransmutationRecipeSerializer;
import com.chinaex123.brush_plus.crafting.serializer.EntityHarvestingRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber
public class BPRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, BrushPlus.MODID);

    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> BLOCK_TRANSMUTATION_SERIALIZER = RECIPE_SERIALIZERS.register("block_transmutation", BlockTransmutationRecipeSerializer::new);
    public static DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> ENTITY_HARVESTING_SERIALIZER = RECIPE_SERIALIZERS.register("entity_harvesting", EntityHarvestingRecipeSerializer::new);

    public static RecipeType<BlockTransmutationRecipe> BLOCK_TRANSMUTATION_RECIPE;
    public static RecipeType<EntityHarvestingRecipe> ENTITY_HARVESTING_RECIPE;

    @SubscribeEvent
    public static void register(RegisterEvent evt) {
        if (evt.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
            BLOCK_TRANSMUTATION_RECIPE = RecipeType.simple(BrushPlus.id("block_transmutation"));
            ENTITY_HARVESTING_RECIPE = RecipeType.simple(BrushPlus.id("entity_harvesting"));
        }
    }
}