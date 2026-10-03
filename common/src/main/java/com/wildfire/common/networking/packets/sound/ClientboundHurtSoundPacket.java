package com.wildfire.common.networking.packets.sound;

import com.wildfire.common.WildfireGender;
import com.wildfire.common.entitydata.PlayerConfigHolder;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/** Tells modded clients which nearby player produced a female hurt sound. */
public record ClientboundHurtSoundPacket(UUID playerId) implements CustomPacketPayload {
    public static final Type<ClientboundHurtSoundPacket> TYPE = WildfireGender.clientBoundPacket("hurt_sound");
    public static final StreamCodec<ByteBuf, ClientboundHurtSoundPacket> STREAM_CODEC =
            UUIDUtil.STREAM_CODEC.map(ClientboundHurtSoundPacket::new, ClientboundHurtSoundPacket::playerId);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || playerId.equals(client.player.getUUID())) return;

        Player source = client.level.getPlayerByUUID(playerId);
        if (source == null) return;

        PlayerConfigHolder config = WildfireGender.getPlayerByPlayer(source);
        if (config != null) {
            config.tryPlayHurtSound(source);
        }
    }
}
