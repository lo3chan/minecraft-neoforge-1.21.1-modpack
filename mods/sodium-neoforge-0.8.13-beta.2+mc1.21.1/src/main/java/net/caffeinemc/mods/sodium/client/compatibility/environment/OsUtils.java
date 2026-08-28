/*
 * Decompiled with CFR 0.152.
 */
package net.caffeinemc.mods.sodium.client.compatibility.environment;

import java.util.Locale;

public class OsUtils {
    private static final OperatingSystem OS = OsUtils.determineOs();

    public static OperatingSystem determineOs() {
        String name = System.getProperty("os.name");
        if (name != null) {
            String normalized = name.toLowerCase(Locale.ROOT);
            if (normalized.startsWith("windows")) {
                return OperatingSystem.WIN;
            }
            if (normalized.startsWith("mac")) {
                return OperatingSystem.MAC;
            }
            if (normalized.startsWith("linux")) {
                return OperatingSystem.LINUX;
            }
        }
        return OperatingSystem.UNKNOWN;
    }

    public static OperatingSystem getOs() {
        return OS;
    }

    public static enum OperatingSystem {
        WIN,
        MAC,
        LINUX,
        UNKNOWN;

    }
}

