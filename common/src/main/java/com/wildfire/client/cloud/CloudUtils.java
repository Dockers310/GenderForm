package com.wildfire.client.cloud;

/**
 * Утилиты облачной синхронизации.
 *
 * В GenderForm профиль игрока для серверной синхронизации определяется по нику,
 * поэтому проверка Mojang session service больше не используется.
 */
public final class CloudUtils {
    private CloudUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Legacy-проверка оригинального облачного синхронизатора.
     * Для нашей никовой синхронизации она всегда возвращает false.
     */
    static boolean hasTheSessionServiceBeenTamperedWith() {
        return false;
    }
}
