package com.chinaex123.brush_plus.event;

import com.chinaex123.brush_plus.crafting.recipe.BlockTransmutationRecipe;
import com.chinaex123.brush_plus.init.BPRecipes;
import com.chinaex123.brush_plus.network.BrushProgressS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BrushBlockTransmutationHandler {

    private static final Map<String, Integer> PROGRESS_MAP = new HashMap<>();

    public static void clearAllProgress(ServerPlayer player) {
        String uuid = player.getStringUUID();
        var keys = PROGRESS_MAP.keySet().stream()
                .filter(k -> k.startsWith(uuid + ":"))
                .toList();
        for (String key : keys) {
            PROGRESS_MAP.remove(key);
            long posAsLong = Long.parseLong(key.substring(uuid.length() + 1));
            BlockPos pos = BlockPos.of(posAsLong);
            PacketDistributor.sendToPlayer(player, new BrushProgressS2CPacket(pos, 0, 0));
        }
    }

    public static void onBrushTick(ServerPlayer player, ItemStack heldItem) {
        Level level = player.level();
        if (!(heldItem.getItem() instanceof BrushItem)) return;

        HitResult hit = player.pick(5.0D, 0.0F, false);
        if (!(hit instanceof BlockHitResult blockHit)) return;
        if (blockHit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);

        String progressKey = player.getStringUUID() + ":" + pos.asLong();

        BlockTransmutationRecipe.Input input = new BlockTransmutationRecipe.Input() {
            @Override
            public @NotNull ItemStack getBlockItem() {
                return state.getBlock().asItem().getDefaultInstance();
            }

            @Override
            public @NotNull ItemStack getBrushItem() {
                return heldItem;
            }
        };

        List<RecipeHolder<BlockTransmutationRecipe>> recipes =
                level.getRecipeManager().getAllRecipesFor(BPRecipes.BLOCK_TRANSMUTATION_RECIPE);

        Optional<BlockTransmutationRecipe> matchedRecipe = recipes.stream()
                .map(RecipeHolder::value)
                .filter(r -> r.matches(input, level))
                .findFirst();

        if (matchedRecipe.isEmpty()) {
            if (PROGRESS_MAP.containsKey(progressKey)) {
                PROGRESS_MAP.remove(progressKey);
                PacketDistributor.sendToPlayer(player, new BrushProgressS2CPacket(pos, 0, 0));
            }
            return;
        }

        BlockTransmutationRecipe recipe = matchedRecipe.get();
        long gameTime = level.getGameTime();

        if (gameTime % 4 == 0) {
            spawnBrushParticles(level, pos, state);
        }

        int currentProgress = PROGRESS_MAP.getOrDefault(progressKey, 0) + 1;

        PacketDistributor.sendToPlayer(player, new BrushProgressS2CPacket(pos, currentProgress, recipe.totalTicks()));

        if (currentProgress >= recipe.totalTicks()) {
            PROGRESS_MAP.remove(progressKey);
            PacketDistributor.sendToPlayer(player, new BrushProgressS2CPacket(pos, 0, 0));

            ResourceLocation resultBlockId = recipe.rollResultBlock(level.getRandom());
            if (resultBlockId != null) {
                Block block = BuiltInRegistries.BLOCK.get(resultBlockId);
                level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
            }

            for (BlockTransmutationRecipe.DropEntry entry : recipe.validDrops()) {
                if (level.getRandom().nextFloat() < entry.chance()) {
                    ItemStack dropStack = entry.item().copy();
                    ItemEntity itemEntity = new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5,
                            dropStack
                    );
                    itemEntity.setDeltaMovement(
                            (level.getRandom().nextDouble() - 0.5) * 0.15,
                            0.35,
                            (level.getRandom().nextDouble() - 0.5) * 0.15
                    );
                    itemEntity.setPickUpDelay(10);
                    level.addFreshEntity(itemEntity);
                }
            }

            InteractionHand hand = player.getUsedItemHand();
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            heldItem.hurtAndBreak(1, player, slot);
        } else {
            PROGRESS_MAP.put(progressKey, currentProgress);
        }
    }

    private static void spawnBrushParticles(Level level, BlockPos pos, BlockState state) {
        ParticleOptions options = new BlockParticleOption(ParticleTypes.BLOCK, state);
        ServerLevel serverLevel = (ServerLevel) level;

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        for (int i = 0; i < 8; i++) {
            double ox = (level.getRandom().nextDouble() - 0.5) * 0.5;
            double oy = level.getRandom().nextDouble() * 0.5;
            double oz = (level.getRandom().nextDouble() - 0.5) * 0.5;
            serverLevel.sendParticles(options,
                    x + ox, y + oy, z + oz,
                    1, 0, 0, 0, 0.01);
        }
    }
}