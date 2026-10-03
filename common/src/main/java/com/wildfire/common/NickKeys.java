/*
 * Nickname-only identity used by the NickBound fork.
 */
package com.wildfire.common;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

/** Stable identity for a player derived only from the nickname. */
public final class NickKeys {
    public static final String KEY_PREFIX = "FemaleGenderMod:nick:";
    private static final Map<UUID, String> KNOWN_NAMES = new ConcurrentHashMap<>();

    private NickKeys() {}

    public static UUID keyForName(final String name) {
        UUID key = UUID.nameUUIDFromBytes((KEY_PREFIX + name).getBytes(StandardCharsets.UTF_8));
        KNOWN_NAMES.putIfAbsent(key, name);
        return key;
    }

    public static UUID keyOf(final Entity entity) {
        return entity instanceof Player player ? keyForName(player.getName().getString()) : entity.getUUID();
    }

    public static @Nullable String nameOf(final UUID key) {
        return KNOWN_NAMES.get(key);
    }

    public static String toFileName(final String name) {
        return "nick_" + name.replaceAll("[^A-Za-z0-9_.-]", "_");
    }

    public static String toFileNameForKey(final UUID key) {
        String name = nameOf(key);
        return name == null ? "nick_key_" + key : toFileName(name);
    }
}
