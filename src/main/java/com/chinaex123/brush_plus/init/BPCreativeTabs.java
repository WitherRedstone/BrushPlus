package com.chinaex123.brush_plus.init;

import com.chinaex123.brush_plus.BrushPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface BPCreativeTabs {
    DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BrushPlus.MODID);

    DeferredHolder<CreativeModeTab, CreativeModeTab> BRUSH_PLUS =
            CREATIVE_MODE_TABS.register(BrushPlus.MODID, () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(BPItems.DIAMOND_BRUSH.get()))
                    .title(Component.translatable("itemGroup.brush_plus"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(BPItems.IRON_BRUSH);
                        output.accept(BPItems.GOLD_BRUSH);
                        output.accept(BPItems.DIAMOND_BRUSH);
                        output.accept(BPItems.NETHERITE_BRUSH);

                    })
                    .build());

    static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}