package com.chinaex123.brush_plus.crafting.serializer;

import com.chinaex123.brush_plus.crafting.recipe.BlockTransmutationRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BlockTransmutationRecipeSerializer implements RecipeSerializer<BlockTransmutationRecipe> {

    public static final int DEFAULT_TOTAL_TICKS = 80;

    private static final Codec<BlockTransmutationRecipe.DropEntry> DROP_ENTRY_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ItemStack.STRICT_CODEC.fieldOf("item").forGetter(BlockTransmutationRecipe.DropEntry::item),
                    Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(BlockTransmutationRecipe.DropEntry::chance)
            ).apply(inst, BlockTransmutationRecipe.DropEntry::new));

    private static final Codec<BlockTransmutationRecipe.ResultBlockEntry> RESULT_BLOCK_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ResourceLocation.CODEC.fieldOf("block").forGetter(BlockTransmutationRecipe.ResultBlockEntry::blockId),
                    Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(BlockTransmutationRecipe.ResultBlockEntry::chance)
            ).apply(inst, BlockTransmutationRecipe.ResultBlockEntry::new));

    private static final MapCodec<BlockTransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(BlockTransmutationRecipe::ingredient),
            Codec.list(RESULT_BLOCK_CODEC).optionalFieldOf("result_blocks")
                    .forGetter(r -> r.resultBlocks().isEmpty() ? Optional.empty() : Optional.of(r.resultBlocks())),
            ResourceLocation.CODEC.optionalFieldOf("result_block").forGetter(r -> Optional.empty()),
            Codec.list(DROP_ENTRY_CODEC).optionalFieldOf("drops")
                    .forGetter(r -> r.drops().isEmpty() ? Optional.empty() : Optional.of(r.drops())),
            ItemStack.STRICT_CODEC.optionalFieldOf("drop").forGetter(r -> Optional.empty()),
            Codec.FLOAT.optionalFieldOf("drop_chance")
                    .forGetter(r -> Optional.empty()),
            Codec.INT.optionalFieldOf("total_ticks", DEFAULT_TOTAL_TICKS).forGetter(BlockTransmutationRecipe::totalTicks)
    ).apply(inst, (ing, optNewBlocks, optLegacyBlock, optDrops,
                   optLegacyDrop, optLegacyChance, ticks) -> {
        // 合并结果方块
        List<BlockTransmutationRecipe.ResultBlockEntry> resultBlocks = optNewBlocks
                .orElseGet(() -> optLegacyBlock
                        .map(id -> List.of(new BlockTransmutationRecipe.ResultBlockEntry(id)))
                        .orElse(Collections.emptyList()));
        // 合并掉落
        List<BlockTransmutationRecipe.DropEntry> drops = optDrops
                .orElseGet(() -> optLegacyDrop
                        .filter(s -> !s.isEmpty())
                        .map(s -> List.of(new BlockTransmutationRecipe.DropEntry(
                                s, optLegacyChance.orElse(1.0f))))
                        .orElse(Collections.emptyList()));
        return new BlockTransmutationRecipe(ing, resultBlocks, drops, ticks);
    }));

    private static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> SAFE_ITEMSTACK_CODEC = new StreamCodec<>() {
        @Override
        public ItemStack decode(RegistryFriendlyByteBuf buf) {
            if (buf.readBoolean()) return ItemStack.STREAM_CODEC.decode(buf);
            return ItemStack.EMPTY;
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ItemStack stack) {
            if (stack.isEmpty()) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                ItemStack.STREAM_CODEC.encode(buf, stack);
            }
        }
    };

    private static final StreamCodec<RegistryFriendlyByteBuf, BlockTransmutationRecipe.DropEntry> DROP_ENTRY_STREAM_CODEC =
            StreamCodec.composite(
                    SAFE_ITEMSTACK_CODEC, BlockTransmutationRecipe.DropEntry::item,
                    ByteBufCodecs.FLOAT, BlockTransmutationRecipe.DropEntry::chance,
                    BlockTransmutationRecipe.DropEntry::new
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<BlockTransmutationRecipe.DropEntry>> DROP_LIST_CODEC =
            new StreamCodec<>() {
                @Override
                public List<BlockTransmutationRecipe.DropEntry> decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<BlockTransmutationRecipe.DropEntry> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) list.add(DROP_ENTRY_STREAM_CODEC.decode(buf));
                    return list;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, List<BlockTransmutationRecipe.DropEntry> list) {
                    buf.writeVarInt(list.size());
                    for (BlockTransmutationRecipe.DropEntry d : list) DROP_ENTRY_STREAM_CODEC.encode(buf, d);
                }
            };

    private static final StreamCodec<RegistryFriendlyByteBuf, BlockTransmutationRecipe.ResultBlockEntry> RESULT_BLOCK_ENTRY_STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, BlockTransmutationRecipe.ResultBlockEntry::blockId,
                    ByteBufCodecs.FLOAT, BlockTransmutationRecipe.ResultBlockEntry::chance,
                    BlockTransmutationRecipe.ResultBlockEntry::new
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<BlockTransmutationRecipe.ResultBlockEntry>> RESULT_BLOCK_LIST_CODEC =
            new StreamCodec<>() {
                @Override
                public List<BlockTransmutationRecipe.ResultBlockEntry> decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<BlockTransmutationRecipe.ResultBlockEntry> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) list.add(RESULT_BLOCK_ENTRY_STREAM_CODEC.decode(buf));
                    return list;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, List<BlockTransmutationRecipe.ResultBlockEntry> list) {
                    buf.writeVarInt(list.size());
                    for (BlockTransmutationRecipe.ResultBlockEntry e : list) RESULT_BLOCK_ENTRY_STREAM_CODEC.encode(buf, e);
                }
            };

    private static final StreamCodec<RegistryFriendlyByteBuf, BlockTransmutationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, BlockTransmutationRecipe::ingredient,
            RESULT_BLOCK_LIST_CODEC, BlockTransmutationRecipe::resultBlocks,
            DROP_LIST_CODEC, BlockTransmutationRecipe::drops,
            ByteBufCodecs.VAR_INT, BlockTransmutationRecipe::totalTicks,
            BlockTransmutationRecipe::new
    );

    @Override
    public MapCodec<BlockTransmutationRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BlockTransmutationRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}