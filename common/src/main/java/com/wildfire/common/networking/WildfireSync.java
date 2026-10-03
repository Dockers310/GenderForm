/*
 * Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
 * Copyright (C) 2023-present WildfireRomeo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wildfire.common.networking;

import com.wildfire.common.WildfireGender;
import com.wildfire.common.entitydata.PlayerConfig;
import com.wildfire.common.entitydata.PlayerConfigHolder;
import com.wildfire.common.networking.packets.sync.ClientboundSyncPacket;
import com.wildfire.common.networking.packets.sync.ServerboundSyncPacket;
import com.wildfire.common.networking.packets.sound.ClientboundHurtSoundPacket;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class WildfireSync {

    public static final Marker MARKER = MarkerFactory.getMarker("SYNC");

    private WildfireSync() {
        throw new UnsupportedOperationException();
    }

    /// Sync a player's configuration to all nearby connected players
    ///
    /// @param toSync       The [`player`][ServerPlayer] to sync
    /// @param playerConfig The [`configuration`][PlayerConfigHolder] for the target player
    public static void sendToAllClients(ServerPlayer toSync, PlayerConfigHolder playerConfig) {
        int sent = 0;
        for (ServerPlayer player : WildfireNetworking.INSTANCE.playersTracking(toSync)) {
            if (!player.equals(toSync) && WildfireNetworking.INSTANCE.canSyncToPlayer(player)) {
                sent++;
                WildfireNetworking.INSTANCE.syncToPlayer(player, new ClientboundSyncPacket(playerConfig));
            }
        }
        if (sent > 0) {
            WildfireGender.LOGGER.debug(MARKER, "Sent sync packet for {} to {} connected player(s)", toSync, sent);
        }
    }

    /// Sync a player's configuration to another connected player
    ///
    /// @param sendTo The [`player`][ServerPlayer] to send the sync to
    /// @param toSync The [`configuration`][PlayerConfig] for the player being synced
    public static void sendToClient(ServerPlayer sendTo, PlayerConfigHolder toSync) {
        if (WildfireNetworking.INSTANCE.canSyncToPlayer(sendTo)) {
            WildfireGender.LOGGER.debug(MARKER, "Sending profile for {} to other player {}", toSync.uuid, sendTo.getUUID());
            WildfireNetworking.INSTANCE.syncToPlayer(sendTo, new ClientboundSyncPacket(toSync));
        }
    }

    /// Send the client player's configuration to the server for syncing to other players
    ///
    /// @param plr The [`configuration`][PlayerConfig] for the client player
    ///
    /// @apiNote Only call on the client
    /** Broadcast a hurt-sound event to other modded clients near the source player. */
    public static void sendHurtSoundToAllClients(ServerPlayer source) {
        int sent = 0;
        for (ServerPlayer player : WildfireNetworking.INSTANCE.playersTracking(source)) {
            if (!player.equals(source) && WildfireNetworking.INSTANCE.canSyncHurtSoundToPlayer(player)) {
                sent++;
                WildfireNetworking.INSTANCE.syncHurtSoundToPlayer(player, new ClientboundHurtSoundPacket(source.getUUID()));
            }
        }
        if (sent > 0) {
            WildfireGender.LOGGER.debug(MARKER, "Sent hurt sound event for {} to {} player(s)", source, sent);
        }
    }

    /** Send the local player's hurt event to a supporting server bridge. */
    public static void sendHurtSoundToServer(Connection connection) {
        if (WildfireNetworking.INSTANCE.canSyncToServer(connection)) {
            WildfireNetworking.INSTANCE.syncHurtSoundToServer();
        }
    }

    public static void sendToServer(Connection connection, PlayerConfigHolder plr) {
        if (plr.needsSync && WildfireNetworking.INSTANCE.canSyncToServer(connection)) {
            WildfireGender.LOGGER.debug(MARKER, "Sending player data to server");
            WildfireNetworking.INSTANCE.syncToServer(new ServerboundSyncPacket(plr.config()));
            plr.needsSync = false;
        }
    }
}
