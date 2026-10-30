package com.chinaex123.brush_plus.crafting.recipe;

import com.chinaex123.brush_plus.init.BPRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record EntityHarvestingRecipe(
        ResourceLocation entityId,
        List<DropEntry> drops,
        int totalTicks
) implements Recipe<EntityHarvestingRecipe.Input> {

    public record DropEntry(ItemStack item, float chance) {
        public DropEntry(ItemStack item) {
            this(item, 1.0f);
        }
    }

    public EntityHarvestingRecipe(ResourceLocation entityId, int totalTicks) {
        this(entityId, Collections.emptyList(), totalTicks);
    }

    public EntityHarvestingRecipe(ResourceLocation entityId, ItemStack drop, float dropChance, int totalTicks) {
        this(entityId,
                drop.isEmpty() ? Collections.emptyList()
                        : List.of(new DropEntry(drop, dropChance)),
                totalTicks);
    }

    public EntityHarvestingRecipe(ResourceLocation entityId, ItemStack drop, int totalTicks) {
        this(entityId, drop, 1.0f, totalTicks);
    }

    public boolean hasAnyDrop() {
        for (DropEntry d : drops) {
            if (!d.item().isEmpty() && d.chance() > 0.0f) return true;
        }
        return false;
    }

    public List<DropEntry> validDrops() {
        List<DropEntry> result = new ArrayList<>();
        for (DropEntry d : drops) {
            if (!d.item().isEmpty() && d.chance() > 0.0f) result.add(d);
        }
        return result;
    }

    public interface Input extends RecipeInput {
        ResourceLocation getEntityId();

        @Override
        default int size() {
            return 1;
        }

        @Override
        default ItemStack getItem(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        default boolean isEmpty() {
            return false;
        }
    }

    @Override
    public boolean matches(Input input, Level level) {
        return entityId.equals(input.getEntityId());
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registryAccess) {
        return drops.isEmpty() ? ItemStack.EMPTY : drops.get(0).item().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return drops.isEmpty() ? ItemStack.EMPTY : drops.getFirst().item();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BPRecipes.ENTITY_HARVESTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BPRecipes.ENTITY_HARVESTING_RECIPE;
    }
}