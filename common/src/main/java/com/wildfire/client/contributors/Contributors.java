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

package com.wildfire.client.contributors;

import com.wildfire.common.NickKeys;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.Optionull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/// @apiNote Only use this on the client side
public final class Contributors {
    private Contributors() {
        throw new UnsupportedOperationException();
    }

    /** Nickname-bound key for the current creator of this fork. */
    public static final UUID CREATOR_UUID = NickKeys.keyForName("Dok_Si");

    private static final Map<UUID, Contributor> CONTRIBUTORS = new LinkedHashMap<>();

    static {
        // The public credits of this fork intentionally contain only the current project creator.
        // Original upstream contributors remain acknowledged in the source/license notices, but are not
        // presented as members of this fork's team in the in-game credits.
        addContributor(CREATOR_UUID, "Dok_Si", Contributor.Role.MOD_CREATOR);
    }

    /**
     * Returns the contributors shown by the fork's local credits list.
     *
     * <p>The list is intentionally local and contains only explicitly added contributors.
     * This keeps the removed upstream contributors out of the in-game credits.</p>
     */
    public static Map<UUID, Contributor> getContributors() {
        return Collections.unmodifiableMap(CONTRIBUTORS);
    }

    /** Returns the nickname-bound profile keys of contributors shown in credits. */
    public static Set<UUID> getContributorUUIDs() {
        return getContributors().keySet();
    }

    private static <T> @Nullable T map(UUID uuid, Function<Contributor, @Nullable T> mapping) {
        return Optionull.map(getContributors().get(uuid), mapping);
    }

    public static Contributor.@Nullable Role getRole(UUID uuid) {
        return map(uuid, Contributor::getRole);
    }

    public static @Nullable Component getNametag(UUID uuid) {
        return map(uuid, Contributor::asText);
    }

    public static @Nullable TextColor getColor(UUID uuid) {
        return map(uuid, Contributor::getColor);
    }

    private static void addContributor(UUID uuid, String name, Contributor.Role role) {
        if (CONTRIBUTORS.containsKey(uuid)) {
            throw new IllegalArgumentException("Contributor with UUID '" + uuid + "' is already present");
        }

        CONTRIBUTORS.put(uuid, new Contributor(role.bit(), null, name, true));
    }
}
