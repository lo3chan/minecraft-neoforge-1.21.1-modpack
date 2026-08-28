/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Monitor
 *  net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 */
package me.flashyreese.mods.sodiumextra.common.util;

import com.mojang.blaze3d.platform.Monitor;
import me.flashyreese.mods.sodiumextra.client.fog.FogDistanceHelper;
import net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public interface ControlValueFormatterExtended
extends ControlValueFormatter {
    public static ControlValueFormatter resolution() {
        return v -> {
            Monitor monitor = Minecraft.getInstance().getWindow().findBestMonitor();
            if (monitor == null || monitor.getModeCount() <= 0) {
                return Component.translatable((String)"options.fullscreen.unavailable");
            }
            int modeIndex = Math.max(0, Math.min(v - 1, monitor.getModeCount() - 1));
            return v == 0 ? Component.translatable((String)"options.fullscreen.current") : Component.literal((String)monitor.getMode(modeIndex).toString().replace(" (24bit)", ""));
        };
    }

    public static ControlValueFormatter fogDistance() {
        return v -> {
            if (v == 0) {
                return Component.translatable((String)"options.gamma.default");
            }
            if (FogDistanceHelper.disablesFog(v)) {
                return Component.translatable((String)"options.off");
            }
            return Component.translatable((String)"options.chunks", (Object[])new Object[]{v});
        };
    }

    public static ControlValueFormatter protectedFogDistance() {
        return v -> {
            if (v == 0) {
                return Component.translatable((String)"options.gamma.default");
            }
            if (FogDistanceHelper.disablesFog(v)) {
                return Component.translatable((String)"options.off");
            }
            return Component.translatable((String)"sodium-extra.units.blocks", (Object[])new Object[]{v});
        };
    }

    public static ControlValueFormatter ticks() {
        return v -> Component.translatable((String)"sodium-extra.units.ticks", (Object[])new Object[]{v});
    }
}

