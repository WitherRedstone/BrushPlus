package com.chinaex123.brush_plus.mixin;

import com.chinaex123.brush_plus.event.BrushBlockTransmutationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public class BrushItemMixin {

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void brushPlus$onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration, CallbackInfo ci) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        BrushBlockTransmutationHandler.onBrushTick(player, stack);
    }
}