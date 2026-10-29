package com.chinaex123.brush_plus.data.builder;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.crafting.recipe.BlockTransmutationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockTransmutationRecipeBuilder {

    private Ingredient ingredient;
    private final List<BlockTransmutationRecipe.ResultBlockEntry> resultBlocks = new ArrayList<>();
    private final List<BlockTransmutationRecipe.DropEntry> drops = new ArrayList<>();
    private int totalTicks = 80;

    public static BlockTransmutationRecipeBuilder builder() {
        return new BlockTransmutationRecipeBuilder();
    }

    public BlockTransmutationRecipeBuilder setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public BlockTransmutationRecipeBuilder setIngredient(Block block) {
        this.ingredient = Ingredient.of(block);
        return this;
    }

    public BlockTransmutationRecipeBuilder setIngredient(TagKey<Item> itemTag) {
        this.ingredient = Ingredient.of(itemTag);
        return this;
    }

    public BlockTransmutationRecipeBuilder addResultBlock(Block block, float chance) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        resultBlocks.add(new BlockTransmutationRecipe.ResultBlockEntry(id, chance));
        return this;
    }

    public BlockTransmutationRecipeBuilder addResultBlock(Block block) {
        return addResultBlock(block, 1.0f);
    }

    public BlockTransmutationRecipeBuilder addResultBlock(ResourceLocation blockId, float chance) {
        resultBlocks.add(new BlockTransmutationRecipe.ResultBlockEntry(blockId, chance));
        return this;
    }

    public BlockTransmutationRecipeBuilder addResultBlock(ResourceLocation blockId) {
        return addResultBlock(blockId, 1.0f);
    }

    /** 清空所有结果方块（方块不变） */
    public BlockTransmutationRecipeBuilder noResultBlock() {
        resultBlocks.clear();
        return this;
    }

    public BlockTransmutationRecipeBuilder addDrop(ItemStack item, float chance) {
        if (drops.size() >= 3) {
            throw new IllegalStateException("[BrushPlus.BlockTransmutationRecipeBuilder.addDrop]最多只能有 3 个掉落物");
        }
        drops.add(new BlockTransmutationRecipe.DropEntry(item, chance));
        return this;
    }

    public BlockTransmutationRecipeBuilder addDrop(ItemStack item) {
        return addDrop(item, 1.0f);
    }

    public BlockTransmutationRecipeBuilder addDrop(net.minecraft.world.item.Item item, float chance) {
        return addDrop(new ItemStack(item), chance);
    }

    public BlockTransmutationRecipeBuilder addDrop(net.minecraft.world.item.Item item) {
        return addDrop(new ItemStack(item), 1.0f);
    }

    public BlockTransmutationRecipeBuilder addDrop(ResourceLocation itemId, float chance) {
        return addDrop(new ItemStack(BuiltInRegistries.ITEM.get(itemId)), chance);
    }

    public BlockTransmutationRecipeBuilder addDrop(ResourceLocation itemId) {
        return addDrop(itemId, 1.0f);
    }

    public BlockTransmutationRecipeBuilder addDrop(ResourceLocation itemId, int count, float chance) {
        return addDrop(new ItemStack(BuiltInRegistries.ITEM.get(itemId), count), chance);
    }

    /** 清空所有掉落 */
    public BlockTransmutationRecipeBuilder noDrop() {
        drops.clear();
        return this;
    }

    public BlockTransmutationRecipeBuilder setTotalTicks(int ticks) {
        this.totalTicks = ticks;
        return this;
    }

    public void save(RecipeOutput output) {
        String name = !resultBlocks.isEmpty() ? resultBlocks.getFirst().blockId().getPath()
                : (ingredient != null ? "custom" : "unknown");
        save(output, ResourceLocation.fromNamespaceAndPath(BrushPlus.MODID,
                "block_transmutation/" + name));
    }

    public void save(RecipeOutput output, String name) {
        save(output, ResourceLocation.fromNamespaceAndPath(BrushPlus.MODID,
                "block_transmutation/" + name));
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        BlockTransmutationRecipe recipe = new BlockTransmutationRecipe(
                ingredient,
                new ArrayList<>(resultBlocks),
                drops.stream().filter(d -> !d.item().isEmpty()).toList(),
                totalTicks);
        output.accept(id, recipe, null);
    }
}