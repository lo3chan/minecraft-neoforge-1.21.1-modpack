/*
 * Decompiled with CFR 0.152.
 */
package dev.tr7zw.waveycapes.versionless.config;

import dev.tr7zw.waveycapes.versionless.config.Config;

public class ConfigUpgrader {
    public static boolean upgradeConfig(Config config) {
        boolean changed = false;
        if (config.configVersion == 1) {
            config.configVersion = 2;
            if (config.gravity < 0) {
                config.gravity *= -1;
            }
        }
        return changed;
    }
}

