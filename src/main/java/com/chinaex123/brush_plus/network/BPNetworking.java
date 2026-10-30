package com.chinaex123.brush_plus.network;

import com.chinaex123.brush_plus.BrushPlus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = BrushPlus.MODID)
public class BPNetworking {

    private BPNetworking() {}

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(BrushPlus.MODID);

        registrar.playToClient(
                BrushProgressS2CPacket.TYPE,
                BrushProgressS2CPacket.STREAM_CODEC,
                BrushProgressS2CPacket::handle
        );

        registrar.playToClient(
                EntityBrushProgressS2CPacket.TYPE,
                EntityBrushProgressS2CPacket.STREAM_CODEC,
                EntityBrushProgressS2CPacket::handle
        );
    }
}