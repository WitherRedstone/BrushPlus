package com.chinaex123.brush_plus.network.client;

import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BrushProgressClientState {

    public static final BrushProgressClientState INSTANCE = new BrushProgressClientState();

    private static final long TIMEOUT_MS = 10_000L;

    private final Map<BlockPos, ProgressInfo> progressMap = new HashMap<>();

    public void update(BlockPos pos, int progress, int totalTicks) {
        if (progress <= 0 || totalTicks <= 0) {
            progressMap.remove(pos);
            return;
        }
        progressMap.put(pos, new ProgressInfo(progress, totalTicks, System.currentTimeMillis()));
    }

    public void remove(BlockPos pos) {
        progressMap.remove(pos);
    }

    public void tick() {
        long now = System.currentTimeMillis();
        progressMap.entrySet().removeIf(e -> now - e.getValue().lastUpdateMillis() > TIMEOUT_MS);
    }

    public Map<BlockPos, ProgressInfo> getActiveProgress() {
        return progressMap;
    }

    @OnlyIn(Dist.CLIENT)
    public record ProgressInfo(int progress, int totalTicks, long lastUpdateMillis) {

        public float getRatio() {
            return totalTicks <= 0 ? 0f : Math.min(1f, (float) progress / totalTicks);
        }
    }
}