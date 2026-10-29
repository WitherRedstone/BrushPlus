package com.chinaex123.brush_plus.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BPClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue RENDER_PROGRESS_ENABLED;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("渲染设置").push("Render Config");
        RENDER_PROGRESS_ENABLED = builder
                .comment("是否在方块上方渲染进度")
                .comment("Render progress above the block?")
                .define("renderProgressEnabled", true);
        builder.pop();

        SPEC = builder.build();
    }
}
