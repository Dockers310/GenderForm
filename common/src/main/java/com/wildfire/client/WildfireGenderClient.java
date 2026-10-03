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

package com.wildfire.client;

import com.google.gson.JsonObject;
import com.wildfire.api.WildfireAPI;
import com.wildfire.common.LoaderAgnostics;
import com.wildfire.common.WildfireGender;
import com.wildfire.common.NickKeys;
import com.wildfire.client.cloud.CloudSync;
import com.wildfire.client.config.ClientConfig;
import com.wildfire.common.config.Configuration;
import com.wildfire.client.contributors.Contributors;
import com.wildfire.common.entitydata.PlayerConfigHolder;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.jspecify.annotations.Nullable;

/// @apiNote Only use this on the client side
public class WildfireGenderClient {
    private static final Executor LOAD_EXECUTOR = Util.ioPool().forName(WildfireAPI.MODID + "$loadPlayerData");

    public static void tryMigrate() {
        tryMigrate("WildfireGender", Configuration.CONFIG_DIR);
        tryMigrate("wildfire_gender.json", WildfireAPI.MODID + ".json");
    }

    private static void tryMigrate(String oldPath, String newPath) {
        Path oldFile = LoaderAgnostics.INSTANCE.getConfigDir().resolve(oldPath);
        Path newFile = LoaderAgnostics.INSTANCE.getConfigDir().resolve(newPath);

        if (Files.notExists(oldFile)) {
            WildfireGender.LOGGER.debug("{} doesn't exist, nothing to migrate", oldPath);
            return;
        } else if (Files.exists(oldFile) && Files.exists(newFile)) {
            WildfireGender.LOGGER.warn("Cannot migrate {} to {} as both exist", oldPath, oldPath);
            return;
        }

        try {
            Files.move(oldFile, newFile);
            WildfireGender.LOGGER.info("Migrated {} to '{}'", oldPath, newFile);
        } catch (IOException e) {
            WildfireGender.LOGGER.error("Failed to move {} to {}", oldPath, newFile, e);
        }
    }

    public static CompletableFuture<@Nullable PlayerConfigHolder> loadGenderInfo(UUID uuid, boolean markForSync, boolean bypassQueue) {
        var client = Minecraft.getInstance();
        var localPlayer = client.player;
        if (localPlayer != null && localPlayer.getUUID().equals(uuid)) {
            return loadGenderInfo(WildfireGender.getOrAddPlayerByPlayer(localPlayer), markForSync, bypassQueue);
        }
        return CompletableFuture.completedFuture(null);
    }

    public static CompletableFuture<PlayerConfigHolder> loadGenderInfo(PlayerConfigHolder player, boolean markForSync, boolean bypassQueue) {
        return CompletableFuture.supplyAsync(() -> {
            if (player.hasLocalConfig()) {
                player.loadFromDisk(markForSync);
            }
            return player;
        }, LOAD_EXECUTOR);
    }

    /** Contributor role tags are not rendered above players. */
    public static @Nullable Component getNametag(UUID uuid) {
        return null;
    }
}
