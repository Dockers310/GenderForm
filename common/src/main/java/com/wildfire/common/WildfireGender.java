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

package com.wildfire.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import com.wildfire.api.WildfireAPI;
import com.wildfire.client.WildfireGenderClient;
import com.wildfire.common.entitydata.PlayerConfigHolder;
import com.wildfire.common.config.Configuration;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.UUID;

public class WildfireGender {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final LoadingCache<UUID, PlayerConfigHolder> CACHE = Util.make(() -> {
        var builder = CacheBuilder.newBuilder();
        // Only automatically expire cache entries on the client; a server may go a decent while without accessing
        // the player cache, and we can't easily re-cache a player's settings on a server, while a client
        // will typically either receive settings from the server in a sync, or simply re-fetch from
        // a local config file or from the cloud.
        // Note that servers will manually invalidate cache entries upon a player disconnecting
        // (see WildfireEventHandler#playerDisconnected).
        if (LoaderAgnostics.INSTANCE.onClient()) {
            // TODO this design is super janky, and has some potential edge case issues around LAN worlds;
            //		notably, connected players could potentially have their configs expire, although this is currently
            //		prevented through further jank with how SyncedPlayerList is implemented (which should also
            //		be addressed along with this)
            // best solution to this issue is likely going to be simply splitting the client & server caches into
            // their own dedicated (Loading)Cache instances at some point in the future.
            // might also be nice to take the opportunity to also properly split the configs into a server/client
            // pattern (like entities are right now), although that's probably not going to be very fun to do
            builder.expireAfterAccess(Duration.ofMinutes(15));
        }
        return builder.build(CacheLoader.from(PlayerConfigHolder::new));
    });

    public static @Nullable PlayerConfigHolder getPlayerById(UUID id) {
        return CACHE.getIfPresent(id);
    }

    public static PlayerConfigHolder getOrAddPlayerById(UUID id) {
        return CACHE.getUnchecked(id);
    }

    public static @Nullable PlayerConfigHolder getPlayerByName(String name) {
        return CACHE.getIfPresent(NickKeys.keyForName(name));
    }

    public static PlayerConfigHolder getOrAddPlayerByPlayer(net.minecraft.world.entity.player.Player player) {
        UUID key = NickKeys.keyForName(player.getName().getString());
        PlayerConfigHolder config = CACHE.getUnchecked(key);
        if (LoaderAgnostics.INSTANCE.onClient()) {
            // One-time migration from the old account-UUID filename, if it exists.
            if (!config.hasLocalConfig()) {
                var configDirectory = LoaderAgnostics.INSTANCE.getConfigDir().resolve(Configuration.CONFIG_DIR);
                var legacy = configDirectory.resolve(player.getUUID() + ".json");
                var current = configDirectory.resolve(NickKeys.toFileName(player.getName().getString()) + ".json");
                if (java.nio.file.Files.exists(legacy)) {
                    try {
                        java.nio.file.Files.copy(legacy, current, java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
                        LOGGER.info("Migrated Female Gender Mod profile for {} from legacy UUID storage", player.getName().getString());
                    } catch (java.io.IOException ex) {
                        LOGGER.warn("Failed to migrate legacy Female Gender Mod profile for {}", player.getName().getString(), ex);
                    }
                }
            }
            var localPlayer = net.minecraft.client.Minecraft.getInstance().player;
            if (localPlayer != null && NickKeys.keyForName(localPlayer.getName().getString()).equals(key)
                    && config.syncStatus == PlayerConfigHolder.SyncStatus.UNKNOWN) {
                config.loadFromDisk(true);
            }
        }
        return config;
    }

    public static @Nullable PlayerConfigHolder getPlayerByPlayer(net.minecraft.world.entity.player.Player player) {
        return getPlayerByName(player.getName().getString());
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(WildfireAPI.MODID, path);
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> clientBoundPacket(String path) {
        return packet("clientbound/" + path);
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> serverBoundPacket(String path) {
        return packet("serverbound/" + path);
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> packet(String path) {
        return new CustomPacketPayload.Type<>(id(path));
    }
}
