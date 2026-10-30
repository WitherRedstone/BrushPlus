package com.chinaex123.brush_plus.event;

import com.chinaex123.brush_plus.crafting.recipe.EntityHarvestingRecipe;
import com.chinaex123.brush_plus.init.BPRecipes;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BrushEntityHarvestingHandler {

    private static final Map<String, Integer> PROGRESS_MAP = new HashMap<>();

    public static void clearAllProgress(ServerPlayer player) {
        String uuid = player.getStringUUID();
        var keys = PROGRESS_MAP.keySet().stream()
                .filter(k -> k.startsWith(uuid + ":"))
                .toList();
        for (String key : keys) {
            PROGRESS_MAP.remove(key);
        }
    }

    public static void onBrushTick(ServerPlayer player, ItemStack heldItem) {
        Level level = player.level();
        if (!(heldItem.getItem() instanceof BrushItem)) return;

        HitResult hit = player.pick(5.0D, 0.0F, false);
        if (!(hit instanceof EntityHitResult entityHit)) return;

        Entity target = entityHit.getEntity();
        ResourceLocation targetEntityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());

        String progressKey = player.getStringUUID() + ":" + target.getId();

        EntityHarvestingRecipe.Input input = new EntityHarvestingRecipe.Input() {
            @Override
            public ResourceLocation getEntityId() {
                return targetEntityId;
            }
        };

        List<RecipeHolder<EntityHarvestingRecipe>> recipes =
                level.getRecipeManager().getAllRecipesFor(BPRecipes.ENTITY_HARVESTING_RECIPE);

        Optional<EntityHarvestingRecipe> matchedRecipe = recipes.stream()
                .map(RecipeHolder::value)
                .filter(r -> r.matches(input, level))
                .findFirst();

        if (matchedRecipe.isEmpty()) {
            PROGRESS_MAP.remove(progressKey);
            return;
        }

        EntityHarvestingRecipe recipe = matchedRecipe.get();
        long gameTime = level.getGameTime();

        if (gameTime % 4 == 0) {
            spawnBrushParticles(level, target);
        }

        int currentProgress = PROGRESS_MAP.getOrDefault(progressKey, 0) + 1;

        if (currentProgress >= recipe.totalTicks()) {
            PROGRESS_MAP.remove(progressKey);

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
        } else {
            PROGRESS_MAP.put(progressKey, currentProgress);
        }
    }

    private static void spawnBrushParticles(Level level, Entity target) {
        ServerLevel serverLevel = (ServerLevel) level;
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