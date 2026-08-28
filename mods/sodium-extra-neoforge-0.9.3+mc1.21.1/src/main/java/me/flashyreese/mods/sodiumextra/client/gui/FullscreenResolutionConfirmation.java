/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.VideoMode
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package me.flashyreese.mods.sodiumextra.client.gui;

import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.Optional;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.gui.FullscreenResolutionConfirmScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class FullscreenResolutionConfirmation {
    private static boolean requested;
    private static Optional<VideoMode> previousMode;

    public static void request(Optional<VideoMode> previousVideoMode) {
        requested = true;
        previousMode = previousVideoMode;
    }

    public static void tick(Minecraft client) {
        if (!requested || client.getWindow() == null) {
            return;
        }
        requested = false;
        Optional<VideoMode> previous = previousMode;
        previousMode = Optional.empty();
        client.setScreen((Screen)new FullscreenResolutionConfirmScreen(previous));
    }

    static void keep() {
        SodiumExtraClientMod.disarmWaylandFullscreenResolutionRecovery();
    }

    static void revert(Optional<VideoMode> previousVideoMode) {
        Window window = Minecraft.getInstance().getWindow();
        window.setPreferredFullscreenVideoMode(previousVideoMode);
        window.changeFullscreenVideoMode();
        Minecraft.getInstance().options.save();
        SodiumExtraClientMod.disarmWaylandFullscreenResolutionRecovery();
    }

    static {
        previousMode = Optional.empty();
    }
}

