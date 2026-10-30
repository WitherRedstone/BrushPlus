package com.chinaex123.brush_plus.client.renderer;

import com.chinaex123.brush_plus.network.client.EntityBrushProgressClientState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class EntityBrushProgressRenderer {

    private static final float BAR_Y_OFFSET = 0.5f;
    private static final float BAR_WIDTH = 0.8f;
    private static final float BAR_HEIGHT = 0.08f;

    public static void renderAll(PoseStack poseStack, Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var progressMap = EntityBrushProgressClientState.INSTANCE.getActiveProgress();
        if (progressMap.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        for (var entry : progressMap.entrySet()) {
            int entityId = entry.getKey();
            EntityBrushProgressClientState.ProgressInfo info = entry.getValue();

            Entity entity = mc.level.getEntity(entityId);
            if (entity == null) continue;

            double x = entity.getX();
            double y = entity.getY() + entity.getBbHeight() + BAR_Y_OFFSET;
            double z = entity.getZ();

            poseStack.pushPose();
            poseStack.translate(
                    x - camera.getPosition().x,
                    y - camera.getPosition().y,
                    z - camera.getPosition().z
            );

            double dx = camera.getPosition().x - x;
            double dz = camera.getPosition().z - z;
            float yaw = (float) Math.toDegrees(Math.atan2(dx, dz));
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

            float ratio = info.getRatio();

            drawRect(poseStack, -BAR_WIDTH / 2, -BAR_HEIGHT / 2,
                    BAR_WIDTH / 2, BAR_HEIGHT / 2, 0x80000000);

            float progressWidth = BAR_WIDTH * ratio;
            int progressColor = getProgressColor(ratio);
            drawRect(poseStack, -BAR_WIDTH / 2, -BAR_HEIGHT / 2,
                    -BAR_WIDTH / 2 + progressWidth, BAR_HEIGHT / 2,
                    progressColor);

            poseStack.popPose();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawRect(PoseStack poseStack, float x1, float y1, float x2, float y2, int argb) {
        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;

        float minX = Math.min(x1, x2);
        float maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);

        Matrix4f matrix = poseStack.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, minX, minY, 0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, 0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, 0f).setColor(r, g, b, a);

        buffer.addVertex(matrix, maxX, minY, 0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, 0f).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, 0f).setColor(r, g, b, a);

        var mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private static int getProgressColor(float ratio) {
        float r, g;
        if (ratio < 0.5f) {
            float t = ratio / 0.5f;
            r = t;
            g = 1f;
        } else {
            float t = (ratio - 0.5f) / 0.5f;
            r = 1f;
            g = 1f - t;
        }
        return ((int) (1f * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8);
    }
}