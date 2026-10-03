package com.wildfire.common.networking.packets.sound;

import com.wildfire.common.WildfireGender;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Empty event packet: the server already knows which player sent it. */
public record ServerboundHurtSoundPacket() implements CustomPacketPayload {
    public static final Type<ServerboundHurtSoundPacket> TYPE = WildfireGender.serverBoundPacket("hurt_sound");
    public static final StreamCodec<ByteBuf, ServerboundHurtSoundPacket> STREAM_CODEC = StreamCodec.unit(new ServerboundHurtSoundPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
