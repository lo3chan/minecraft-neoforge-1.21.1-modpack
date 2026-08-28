/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.theme;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class IconRenderer {
    public static int renderIconWithSpacing(GuiGraphics guiGraphics, ResourceLocation icon, int color, boolean monochrome, int x, int y, int height, int spacing) {
        int iconSize = height - spacing * 2;
        int iconX = x + spacing;
        int iconY = y + height / 2 - iconSize / 2;
        if (monochrome) {
            guiGraphics.setColor((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f);
        }
        guiGraphics.blit(icon, iconX, iconY, 0.0f, 0.0f, iconSize, iconSize, iconSize, iconSize);
        if (monochrome) {
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        return spacing * 2 + iconSize;
    }
}

