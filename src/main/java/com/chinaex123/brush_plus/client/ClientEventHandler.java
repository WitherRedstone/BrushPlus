package com.chinaex123.brush_plus.client;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.client.renderer.BrushProgressRenderer;
import com.chinaex123.brush_plus.client.renderer.EntityBrushProgressRenderer;
import com.chinaex123.brush_plus.config.BPClientConfig;
import com.chinaex123.brush_plus.network.client.BrushProgressClientState;
import com.chinaex123.brush_plus.network.client.EntityBrushProgressClientState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = BrushPlus.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        if (!BPClientConfig.RENDER_PROGRESS_ENABLED.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        PoseStack poseStack = event.getPoseStack();

        RenderSystem.setProjectionMatrix(event.getProjectionMatrix(), VertexSorting.DISTANCE_TO_ORIGIN);

        var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.identity();

        poseStack.pushPose();
        BrushProgressRenderer.renderAll(poseStack, event.getCamera());
        EntityBrushProgressRenderer.renderAll(poseStack, event.getCamera());
        poseStack.popPose();

        modelViewStack.popMatrix();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        BrushProgressClientState.INSTANCE.tick();
        EntityBrushProgressClientState.INSTANCE.tick();
    }
}