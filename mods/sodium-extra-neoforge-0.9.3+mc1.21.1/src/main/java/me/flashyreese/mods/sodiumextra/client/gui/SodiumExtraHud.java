/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.phys.Vec3
 */
package me.flashyreese.mods.sodiumextra.client.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import me.flashyreese.mods.sodiumextra.client.FrameCounter;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.phys.Vec3;

public class SodiumExtraHud {
    private final List<Component> textList = new ObjectArrayList();
    private final Minecraft client = Minecraft.getInstance();
    private final FrameCounter stats = FrameCounter.getInstance();

    public void onStartTick(Minecraft client) {
        MutableComponent text;
        this.textList.clear();
        if (SodiumExtraClientMod.options().extraSettings.showFps) {
            int currentFPS = FrameCounter.getInstance().getSmoothFps();
            text = Component.translatable((String)"sodium-extra.overlay.fps", (Object[])new Object[]{currentFPS});
            if (SodiumExtraClientMod.options().extraSettings.showFPSExtended) {
                text = Component.literal((String)String.format("%s %s", text.getString(), Component.translatable((String)"sodium-extra.overlay.fps_extended", (Object[])new Object[]{this.stats.getAverageFps(), this.stats.getOnePercentLowFps(), this.stats.getPointOnePercentLowFps()}).getString()));
            }
            this.textList.add((Component)text);
        }
        if (SodiumExtraClientMod.options().extraSettings.showCoords && this.client.player != null) {
            Vec3 pos = this.client.player.position();
            text = Component.translatable((String)"sodium-extra.overlay.coordinates", (Object[])new Object[]{String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z)});
            if (this.client.showOnlyReducedInfo()) {
                text = Component.translatable((String)"sodium-extra.overlay.coordinates_unavailable");
            }
            this.textList.add((Component)text);
        }
    }

    public void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!this.client.getDebugOverlay().showDebugScreen() && !this.client.options.hideGui) {
            int n;
            SodiumExtraGameOptions.OverlayCorner overlayCorner = SodiumExtraClientMod.options().extraSettings.overlayCorner;
            if (overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_LEFT || overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT) {
                int n2 = this.client.getWindow().getGuiScaledHeight();
                Objects.requireNonNull(this.client.font);
                n = n2 - 9 - 2;
            } else {
                n = 2;
            }
            int y = n;
            for (Component text : this.textList) {
                int x = overlayCorner == SodiumExtraGameOptions.OverlayCorner.TOP_RIGHT || overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT ? this.client.getWindow().getGuiScaledWidth() - this.client.font.width((FormattedText)text) - 2 : 2;
                this.drawString(guiGraphics, text, x, y);
                if (overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_LEFT || overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT) {
                    Objects.requireNonNull(this.client.font);
                    y -= 9 + 2;
                    continue;
                }
                Objects.requireNonNull(this.client.font);
                y += 9 + 2;
            }
        }
    }

    private void drawString(GuiGraphics guiGraphics, Component text, int x, int y) {
        int textColor = -1;
        if (SodiumExtraClientMod.options().extraSettings.textContrast == SodiumExtraGameOptions.TextContrast.BACKGROUND) {
            int n = x + this.client.font.width((FormattedText)text) + 1;
            Objects.requireNonNull(this.client.font);
            guiGraphics.fill(x - 1, y - 1, n, y + 9 + 1, -1873784752);
        }
        guiGraphics.drawString(this.client.font, text, x, y, textColor, SodiumExtraClientMod.options().extraSettings.textContrast == SodiumExtraGameOptions.TextContrast.SHADOW);
    }
}

