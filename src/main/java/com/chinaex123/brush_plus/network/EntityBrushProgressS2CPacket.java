package com.chinaex123.brush_plus.network;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.network.client.EntityBrushProgressClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EntityBrushProgressS2CPacket(int entityId, int progress, int totalTicks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EntityBrushProgressS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(BrushPlus.id("entity_brush_progress"));

    public static final StreamCodec<FriendlyByteBuf, EntityBrushProgressS2CPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, EntityBrushProgressS2CPacket::entityId,
                    ByteBufCodecs.VAR_INT, EntityBrushProgressS2CPacket::progress,
                    ByteBufCodecs.VAR_INT, EntityBrushProgressS2CPacket::totalTicks,
                    EntityBrushProgressS2CPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EntityBrushProgressS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> EntityBrushProgressClientState.INSTANCE.update(
                packet.entityId(), packet.progress(), packet.totalTicks()
        ));
    }
}