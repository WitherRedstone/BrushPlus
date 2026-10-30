package com.chinaex123.brush_plus.mixin;

import com.chinaex123.brush_plus.event.BrushBlockTransmutationHandler;
import com.chinaex123.brush_plus.event.BrushEntityHarvestingHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public class BrushItemMixin {

    @Inject(method = "onUseTick", at = @At("HEAD"), cancellable = true)
    private void brushPlus$onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration, CallbackInfo ci) {
        ci.cancel();

        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        HitResult hit = ProjectileUtil.getHitResultOnViewVector(
                player,
                (e) -> !e.isSpectator() && e.isPickable(),
                player.blockInteractionRange());

        if (hit instanceof EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            if (BrushEntityHarvestingHandler.isValidTarget(player, target)) {
                BrushEntityHarvestingHandler.onBrushTick(player, stack, target, remainingUseDuration);
            }
            return;
        }

        BrushBlockTransmutationHandler.onBrushTick(player, stack);
    }
}