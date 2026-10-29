package com.chinaex123.brush_plus.crafting.recipe;

import com.chinaex123.brush_plus.init.BPRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;

public record BlockTransmutationRecipe(
        Ingredient ingredient,
        List<ResultBlockEntry> resultBlocks,
        List<DropEntry> drops,
        int totalTicks
) implements Recipe<BlockTransmutationRecipe.Input> {

    public record ResultBlockEntry(ResourceLocation blockId, float chance) {
        public ResultBlockEntry(ResourceLocation blockId) {
            this(blockId, 1.0f);
        }
    }

    public record DropEntry(ItemStack item, float chance) {
        public DropEntry(ItemStack item) {
            this(item, 1.0f);
        }
    }

    public BlockTransmutationRecipe(Ingredient ingredient, ResourceLocation resultBlockId, int totalTicks) {
        this(ingredient,
                resultBlockId == null ? Collections.emptyList() : List.of(new ResultBlockEntry(resultBlockId)),
                Collections.emptyList(), totalTicks);
    }

    public BlockTransmutationRecipe(Ingredient ingredient, ResourceLocation resultBlockId,
                                    ItemStack drop, float dropChance, int totalTicks) {
        this(ingredient,
                resultBlockId == null ? Collections.emptyList()
                        : List.of(new ResultBlockEntry(resultBlockId)),
                drop.isEmpty() ? Collections.emptyList()
                        : List.of(new DropEntry(drop, dropChance)),
                totalTicks);
    }

    public BlockTransmutationRecipe(Ingredient ingredient, ResourceLocation resultBlockId,
                                    ItemStack drop, int totalTicks) {
        this(ingredient, resultBlockId, drop, 1.0f, totalTicks);
    }

    public boolean isKeepBlock() {
        return resultBlocks.isEmpty();
    }

    @Nullable
    public ResourceLocation rollResultBlock(RandomSource random) {
        float r = random.nextFloat();
        float cumulative = 0.0f;
        for (ResultBlockEntry entry : resultBlocks) {
            cumulative += entry.chance();
            if (r < cumulative) return entry.blockId();
        }
        return null;
    }

    @Nullable
    public ResourceLocation primaryResultBlockId() {
        if (resultBlocks.isEmpty()) return null;
        ResultBlockEntry best = resultBlocks.get(0);
        for (ResultBlockEntry e : resultBlocks) {
            if (e.chance() > best.chance()) best = e;
        }
        return best.blockId();
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
        ItemStack getBlockItem();

        default ItemStack getBrushItem() {
            return ItemStack.EMPTY;
        }

        @Override
        default int size() {
            return 1;
        }

        @Override
        default ItemStack getItem(int slot) {
            return slot == 0 ? getBlockItem() : ItemStack.EMPTY;
        }

        @Override
        default boolean isEmpty() {
            return getBlockItem().isEmpty();
        }
    }

    @Override
    public boolean matches(Input input, Level level) {
        return ingredient.test(input.getBlockItem());
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
        return BPRecipes.BLOCK_TRANSMUTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BPRecipes.BLOCK_TRANSMUTATION_RECIPE;
    }

    @Nullable
    public BlockState resultBlockState() {
        ResourceLocation id = primaryResultBlockId();
        if (id == null) return null;
        Block block = BuiltInRegistries.BLOCK.get(id);
        return block.defaultBlockState();
    }
}