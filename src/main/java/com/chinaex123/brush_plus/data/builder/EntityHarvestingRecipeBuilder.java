package com.chinaex123.brush_plus.data.builder;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.crafting.recipe.EntityHarvestingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityHarvestingRecipeBuilder {

    private ResourceLocation entityId;
    private final List<EntityHarvestingRecipe.DropEntry> drops = new ArrayList<>();
    private int totalTicks = 80;

    public static EntityHarvestingRecipeBuilder builder() {
        return new EntityHarvestingRecipeBuilder();
    }

    public EntityHarvestingRecipeBuilder setEntity(ResourceLocation entityId) {
        this.entityId = entityId;
        return this;
    }

    public EntityHarvestingRecipeBuilder setEntity(EntityType<?> entityType) {
        this.entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        return this;
    }

    public EntityHarvestingRecipeBuilder addDrop(ItemStack item, float chance) {
        if (drops.size() >= 3) {
            throw new IllegalStateException("[BrushPlus.EntityHarvestingRecipeBuilder.addDrop]最多只能有 3 个掉落物");
        }
        drops.add(new EntityHarvestingRecipe.DropEntry(item, chance));
        return this;
    }

    public EntityHarvestingRecipeBuilder addDrop(ItemStack item) {
        return addDrop(item, 1.0f);
    }

    public EntityHarvestingRecipeBuilder addDrop(net.minecraft.world.item.Item item, float chance) {
        return addDrop(new ItemStack(item), chance);
    }

    public EntityHarvestingRecipeBuilder addDrop(net.minecraft.world.item.Item item) {
        return addDrop(new ItemStack(item), 1.0f);
    }

    public EntityHarvestingRecipeBuilder addDrop(ResourceLocation itemId, float chance) {
        return addDrop(new ItemStack(BuiltInRegistries.ITEM.get(itemId)), chance);
    }

    public EntityHarvestingRecipeBuilder addDrop(ResourceLocation itemId) {
        return addDrop(itemId, 1.0f);
    }

    public EntityHarvestingRecipeBuilder addDrop(ResourceLocation itemId, int count, float chance) {
        return addDrop(new ItemStack(BuiltInRegistries.ITEM.get(itemId), count), chance);
    }

    public EntityHarvestingRecipeBuilder noDrop() {
        drops.clear();
        return this;
    }

    public EntityHarvestingRecipeBuilder setTotalTicks(int ticks) {
        this.totalTicks = ticks;
        return this;
    }

    public void save(RecipeOutput output) {
        String name = entityId != null ? entityId.getPath() : "unknown";
        save(output, ResourceLocation.fromNamespaceAndPath(BrushPlus.MODID,
                "entity_harvesting/" + name));
    }

    public void save(RecipeOutput output, String name) {
        save(output, ResourceLocation.fromNamespaceAndPath(BrushPlus.MODID,
                "entity_harvesting/" + name));
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        EntityHarvestingRecipe recipe = new EntityHarvestingRecipe(
                entityId,
                drops.stream().filter(d -> !d.item().isEmpty()).toList(),
                totalTicks);
        output.accept(id, recipe, null);
    }
}