package com.chinaex123.brush_plus.network.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class EntityBrushProgressClientState {

    public static final EntityBrushProgressClientState INSTANCE = new EntityBrushProgressClientState();

    private static final long TIMEOUT_MS = 10_000L;

    private final Map<Integer, ProgressInfo> progressMap = new HashMap<>();

    public void update(int entityId, int progress, int totalTicks) {
        if (progress <= 0 || totalTicks <= 0) {
            progressMap.remove(entityId);
            return;
        }
        progressMap.put(entityId, new ProgressInfo(progress, totalTicks, System.currentTimeMillis()));
    }

    public void remove(int entityId) {
        progressMap.remove(entityId);
    }

    public void tick() {
        long now = System.currentTimeMillis();
        progressMap.entrySet().removeIf(e -> now - e.getValue().lastUpdateMillis() > TIMEOUT_MS);
    }

    public Map<Integer, ProgressInfo> getActiveProgress() {
        return progressMap;
    }

    @OnlyIn(Dist.CLIENT)
    public record ProgressInfo(int progress, int totalTicks, long lastUpdateMillis) {

        public float getRatio() {
            return totalTicks <= 0 ? 0f : Math.min(1f, (float) progress / totalTicks);
        }
    }
}