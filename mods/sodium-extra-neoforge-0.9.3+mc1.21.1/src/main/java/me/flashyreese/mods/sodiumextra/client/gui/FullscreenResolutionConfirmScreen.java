/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.VideoMode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.network.chat.Component
 */
package me.flashyreese.mods.sodiumextra.client.gui;

import com.mojang.blaze3d.platform.VideoMode;
import java.util.Optional;
import me.flashyreese.mods.sodiumextra.client.gui.FullscreenResolutionConfirmation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class FullscreenResolutionConfirmScreen
extends ConfirmScreen {
    private static final int TIMEOUT_TICKS = 300;
    private int ticksRemaining = 300;

    public FullscreenResolutionConfirmScreen(Optional<VideoMode> previousMode) {
        super(accepted -> {
            if (accepted) {
                FullscreenResolutionConfirmation.keep();
            } else {
                FullscreenResolutionConfirmation.revert(previousMode);
            }
            Minecraft.getInstance().setScreen(null);
        }, (Component)Component.translatable((String)"sodium-extra.option.wayland_fullscreen_resolution.confirm.title"), (Component)Component.translatable((String)"sodium-extra.option.wayland_fullscreen_resolution.confirm.message"), (Component)Component.translatable((String)"sodium-extra.option.wayland_fullscreen_resolution.confirm.keep"), (Component)Component.translatable((String)"sodium-extra.option.wayland_fullscreen_resolution.confirm.revert"));
    }

    public void tick() {
        super.tick();
        --this.ticksRemaining;
        if (this.ticksRemaining <= 0) {
            this.callback.accept(false);
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}

