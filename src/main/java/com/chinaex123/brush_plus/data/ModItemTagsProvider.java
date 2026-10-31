package com.chinaex123.brush_plus.data;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.init.BPItemTags;
import com.chinaex123.brush_plus.init.BPItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, BrushPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        tag(BPItemTags.BRUSHES)
                .add(BPItems.IRON_BRUSH.get())
                .add(BPItems.GOLD_BRUSH.get())
                .add(BPItems.DIAMOND_BRUSH.get())
                .add(BPItems.NETHERITE_BRUSH.get());
        tag(Tags.Items.TOOLS)
                .add(BPItems.IRON_BRUSH.get())
                .add(BPItems.GOLD_BRUSH.get())
                .add(BPItems.DIAMOND_BRUSH.get())
                .add(BPItems.NETHERITE_BRUSH.get());
        tag(Tags.Items.TOOLS_BRUSH)
                .add(BPItems.IRON_BRUSH.get())
                .add(BPItems.GOLD_BRUSH.get())
                .add(BPItems.DIAMOND_BRUSH.get())
                .add(BPItems.NETHERITE_BRUSH.get());
        tag(Tags.Items.ENCHANTABLES)
                .add(BPItems.IRON_BRUSH.get())
                .add(BPItems.GOLD_BRUSH.get())
                .add(BPItems.DIAMOND_BRUSH.get())
                .add(BPItems.NETHERITE_BRUSH.get());

    }
}