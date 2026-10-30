package com.chinaex123.brush_plus.event;

import com.chinaex123.brush_plus.BrushPlus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BrushItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = BrushPlus.MODID)
public class BPForgeEvents {

    @SubscribeEvent
    public static void onStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getItem().getItem() instanceof BrushItem) {
            BrushBlockTransmutationHandler.clearAllProgress(player);
            BrushEntityHarvestingHandler.clearAllProgress(player);
        }
    }

    @SubscribeEvent
    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getItem().getItem() instanceof BrushItem) {
            BrushBlockTransmutationHandler.clearAllProgress(player);
            BrushEntityHarvestingHandler.clearAllProgress(player);
        }
    }
}