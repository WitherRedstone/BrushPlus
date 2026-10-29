package com.chinaex123.brush_plus.network;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.network.client.BrushProgressClientState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BrushProgressS2CPacket(BlockPos pos, int progress, int totalTicks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BrushProgressS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(BrushPlus.id("brush_progress"));

    public static final StreamCodec<FriendlyByteBuf, BrushProgressS2CPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, BrushProgressS2CPacket::pos,
                    ByteBufCodecs.VAR_INT, BrushProgressS2CPacket::progress,
                    ByteBufCodecs.VAR_INT, BrushProgressS2CPacket::totalTicks,
                    BrushProgressS2CPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BrushProgressS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> BrushProgressClientState.INSTANCE.update(
                packet.pos(), packet.progress(), packet.totalTicks()
        ));
    }
}