package com.chinaex123.brush_plus.compat.jei;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.compat.jei.category.BlockTransmutationRecipeCategory;
import com.chinaex123.brush_plus.compat.jei.category.EntityHarvestingRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID = BrushPlus.id("jei");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new BlockTransmutationRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new EntityHarvestingRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(
                BlockTransmutationRecipeCategory.TYPE,
                BlockTransmutationRecipeCategory.getRecipes()
        );
        registration.addRecipes(
                EntityHarvestingRecipeCategory.TYPE,
                EntityHarvestingRecipeCategory.getRecipes()
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof BrushItem) {
                registration.addRecipeCatalyst(new ItemStack(item), BlockTransmutationRecipeCategory.TYPE);
                registration.addRecipeCatalyst(new ItemStack(item), EntityHarvestingRecipeCategory.TYPE);
            }
        });
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
}