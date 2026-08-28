/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.loading.FMLLoader
 *  net.neoforged.fml.loading.moddiscovery.ModInfo
 */
package net.caffeinemc.caffeineconfig;

import java.util.Map;
import net.caffeinemc.caffeineconfig.CaffeineConfig;
import net.caffeinemc.caffeineconfig.CaffeineConfigPlatform;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public class CaffeineConfigNeoForge
implements CaffeineConfigPlatform {
    @Override
    public void applyModOverrides(CaffeineConfig config, String jsonKey) {
        for (ModInfo meta : FMLLoader.getLoadingModList().getMods()) {
            meta.getConfigElement(new String[]{jsonKey}).ifPresent(override -> {
                Map overrides;
                if (override instanceof Map && (overrides = (Map)override).keySet().stream().allMatch(key -> key instanceof String)) {
                    overrides.forEach((key, value) -> {
                        if (!(value instanceof Boolean) || !(key instanceof String)) {
                            config.getLogger().warn("Mod '{}' attempted to override option '{}' with an invalid value, ignoring", (Object)meta.getModId(), key);
                            return;
                        }
                        config.applyModOverride(meta.getModId(), (String)key, (Boolean)value);
                    });
                } else {
                    config.getLogger().warn("Mod '{}' contains invalid Sodium option overrides, ignoring", (Object)meta.getModId());
                }
            });
        }
    }
}

