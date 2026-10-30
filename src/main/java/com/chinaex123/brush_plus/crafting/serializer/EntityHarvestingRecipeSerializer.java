package com.chinaex123.brush_plus.crafting.serializer;

import com.chinaex123.brush_plus.crafting.recipe.EntityHarvestingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EntityHarvestingRecipeSerializer implements RecipeSerializer<EntityHarvestingRecipe> {

    public static final int DEFAULT_TOTAL_TICKS = 80;

    private static final Codec<EntityHarvestingRecipe.DropEntry> DROP_ENTRY_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    ItemStack.STRICT_CODEC.fieldOf("item").forGetter(EntityHarvestingRecipe.DropEntry::item),
                    Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(EntityHarvestingRecipe.DropEntry::chance)
            ).apply(inst, EntityHarvestingRecipe.DropEntry::new));

    private static final MapCodec<EntityHarvestingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("entity").forGetter(EntityHarvestingRecipe::entityId),
            Codec.list(DROP_ENTRY_CODEC).optionalFieldOf("drops")
                    .forGetter(r -> r.drops().isEmpty() ? Optional.empty() : Optional.of(r.drops())),
            ItemStack.STRICT_CODEC.optionalFieldOf("drop").forGetter(r -> Optional.empty()),
            Codec.FLOAT.optionalFieldOf("drop_chance").forGetter(r -> Optional.empty()),
            Codec.INT.optionalFieldOf("total_ticks", DEFAULT_TOTAL_TICKS).forGetter(EntityHarvestingRecipe::totalTicks)
    ).apply(inst, (entityId, optDrops, optLegacyDrop, optLegacyChance, ticks) -> {
        List<EntityHarvestingRecipe.DropEntry> drops = optDrops
                .orElseGet(() -> optLegacyDrop
                        .filter(s -> !s.isEmpty())
                        .map(s -> List.of(new EntityHarvestingRecipe.DropEntry(
                                s, optLegacyChance.orElse(1.0f))))
                        .orElse(Collections.emptyList()));
        return new EntityHarvestingRecipe(entityId, drops, ticks);
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

    private static final StreamCodec<RegistryFriendlyByteBuf, EntityHarvestingRecipe.DropEntry> DROP_ENTRY_STREAM_CODEC =
            StreamCodec.composite(
                    SAFE_ITEMSTACK_CODEC, EntityHarvestingRecipe.DropEntry::item,
                    ByteBufCodecs.FLOAT, EntityHarvestingRecipe.DropEntry::chance,
                    EntityHarvestingRecipe.DropEntry::new
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<EntityHarvestingRecipe.DropEntry>> DROP_LIST_CODEC =
            new StreamCodec<>() {
                @Override
                public List<EntityHarvestingRecipe.DropEntry> decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<EntityHarvestingRecipe.DropEntry> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) list.add(DROP_ENTRY_STREAM_CODEC.decode(buf));
                    return list;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, List<EntityHarvestingRecipe.DropEntry> list) {
                    buf.writeVarInt(list.size());
                    for (EntityHarvestingRecipe.DropEntry d : list) DROP_ENTRY_STREAM_CODEC.encode(buf, d);
                }
            };

    private static final StreamCodec<RegistryFriendlyByteBuf, EntityHarvestingRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, EntityHarvestingRecipe::entityId,
            DROP_LIST_CODEC, EntityHarvestingRecipe::drops,
            ByteBufCodecs.VAR_INT, EntityHarvestingRecipe::totalTicks,
            EntityHarvestingRecipe::new
    );

    @Override
    public MapCodec<EntityHarvestingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityHarvestingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}