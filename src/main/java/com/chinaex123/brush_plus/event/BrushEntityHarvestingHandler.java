package com.chinaex123.brush_plus.event;

import com.chinaex123.brush_plus.crafting.recipe.EntityHarvestingRecipe;
import com.chinaex123.brush_plus.init.BPRecipes;
import com.chinaex123.brush_plus.network.EntityBrushProgressS2CPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BrushEntityHarvestingHandler {

    private static final Map<String, Integer> PROGRESS_MAP = new HashMap<>();
    private static final Map<String, Long> LAST_TARGET_MAP = new HashMap<>();
    private static final Map<String, Integer> LAST_REMAINING_MAP = new HashMap<>();

    public static boolean isValidTarget(ServerPlayer player, Entity target) {
        if (target == null || !target.isAlive()) return false;
        if (target.isSpectator()) return false;

        Level level = player.level();
        ResourceLocation targetEntityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        EntityHarvestingRecipe.Input input = () -> targetEntityId;

        List<RecipeHolder<EntityHarvestingRecipe>> recipes =
                level.getRecipeManager().getAllRecipesFor(BPRecipes.ENTITY_HARVESTING_RECIPE);

        return recipes.stream()
                .map(RecipeHolder::value)
                .anyMatch(r -> r.matches(input, level));
    }

    public static void clearAllProgress(ServerPlayer player) {
        String uuid = player.getStringUUID();
        var keys = PROGRESS_MAP.keySet().stream()
                .filter(k -> k.startsWith(uuid + ":"))
                .toList();
        for (String key : keys) {
            PROGRESS_MAP.remove(key);
            int entityId = Integer.parseInt(key.substring(uuid.length() + 1));
            PacketDistributor.sendToPlayer(player,
                    new EntityBrushProgressS2CPacket(entityId, 0, 0));
        }
        LAST_TARGET_MAP.remove(uuid);
        LAST_REMAINING_MAP.remove(uuid);
    }

    public static void onBrushTick(ServerPlayer player, ItemStack heldItem, Entity target,
                                   int remainingUseDuration) {
        Level level = player.level();
        if (!(heldItem.getItem() instanceof BrushItem)) return;
        if (target == null || !target.isAlive()) return;

        if (remainingUseDuration > 195) {
            return;
        }

        String uuid = player.getStringUUID();

        Integer lastRemaining = LAST_REMAINING_MAP.get(uuid);
        if (lastRemaining != null && lastRemaining - remainingUseDuration != 1) {
            resetProgress(player, uuid);
        }
        LAST_REMAINING_MAP.put(uuid, remainingUseDuration);

        long currentTarget = target.getId();
        Long lastTarget = LAST_TARGET_MAP.get(uuid);
        if (lastTarget != null && lastTarget != currentTarget) {
            String oldKey = uuid + ":" + lastTarget;
            if (PROGRESS_MAP.containsKey(oldKey)) {
                PROGRESS_MAP.remove(oldKey);
                PacketDistributor.sendToPlayer(player,
                        new EntityBrushProgressS2CPacket((int) (long) lastTarget, 0, 0));
            }
        }
        LAST_TARGET_MAP.put(uuid, currentTarget);

        String progressKey = uuid + ":" + currentTarget;
        ResourceLocation targetEntityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        EntityHarvestingRecipe.Input input = () -> targetEntityId;

        List<RecipeHolder<EntityHarvestingRecipe>> recipes =
                level.getRecipeManager().getAllRecipesFor(BPRecipes.ENTITY_HARVESTING_RECIPE);

        Optional<EntityHarvestingRecipe> matchedRecipe = recipes.stream()
                .map(RecipeHolder::value)
                .filter(r -> r.matches(input, level))
                .findFirst();

        if (matchedRecipe.isEmpty()) {
            if (PROGRESS_MAP.containsKey(progressKey)) {
                PROGRESS_MAP.remove(progressKey);
                PacketDistributor.sendToPlayer(player,
                        new EntityBrushProgressS2CPacket(target.getId(), 0, 0));
            }
            return;
        }

        EntityHarvestingRecipe recipe = matchedRecipe.get();
        long gameTime = level.getGameTime();

        if (gameTime % 4 == 0) {
            spawnBrushParticles(level, target);
        }

        int currentProgress = PROGRESS_MAP.getOrDefault(progressKey, 0) + 1;

        PacketDistributor.sendToPlayer(player,
                new EntityBrushProgressS2CPacket(target.getId(), currentProgress, recipe.totalTicks()));

        if (currentProgress >= recipe.totalTicks()) {
            PROGRESS_MAP.remove(progressKey);
            PacketDistributor.sendToPlayer(player,
                    new EntityBrushProgressS2CPacket(target.getId(), 0, 0));

            for (EntityHarvestingRecipe.DropEntry entry : recipe.validDrops()) {
                if (level.getRandom().nextFloat() < entry.chance()) {
                    ItemStack dropStack = entry.item().copy();
                    ItemEntity itemEntity = new ItemEntity(
                            level,
                            target.getX(),
                            target.getY() + 0.5,
                            target.getZ(),
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

            player.stopUsingItem();
            resetProgress(player, uuid);
        } else {
            PROGRESS_MAP.put(progressKey, currentProgress);
        }
    }

    private static void resetProgress(ServerPlayer player, String uuid) {
        var keys = PROGRESS_MAP.keySet().stream()
                .filter(k -> k.startsWith(uuid + ":"))
                .toList();
        for (String key : keys) {
            PROGRESS_MAP.remove(key);
            int entityId = Integer.parseInt(key.substring(uuid.length() + 1));
            PacketDistributor.sendToPlayer(player,
                    new EntityBrushProgressS2CPacket(entityId, 0, 0));
        }
        LAST_TARGET_MAP.remove(uuid);
    }

    private static void spawnBrushParticles(Level level, Entity target) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() / 2.0;
        double z = target.getZ();
        for (int i = 0; i < 4; i++) {
            double ox = (level.getRandom().nextDouble() - 0.5) * 0.5;
            double oy = (level.getRandom().nextDouble() - 0.5) * target.getBbHeight() * 0.5;
            double oz = (level.getRandom().nextDouble() - 0.5) * 0.5;
            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    x + ox, y + oy, z + oz,
                    1, 0, 0, 0, 0.01);
        }
    }
}