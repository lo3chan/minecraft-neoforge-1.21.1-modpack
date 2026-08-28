/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 */
package me.flashyreese.mods.reeses_sodium_options.client.gui.frame.option.action;

import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.reeses_sodium_options.client.gui.layout.LayoutBounds;
import me.flashyreese.mods.reeses_sodium_options.client.gui.widget.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

final class OptionActionButtonRenderer {
    private static final int ICON_SIZE = 10;
    private static final int BACKGROUND = 0x66000000;
    private static final int BACKGROUND_HOVERED = -1610612736;
    private static final int BACKGROUND_DISABLED = 0x33000000;
    private static final int BORDER_FOCUSED = -1;
    private static final float ICON_ALPHA_DISABLED = 0.4f;

    OptionActionButtonRenderer() {
    }

    static LayoutBounds buttonBounds(LayoutBounds rowBounds, int buttonsFromRight) {
        int size = rowBounds.height();
        int x = rowBounds.getLimitX() - size * buttonsFromRight;
        return new LayoutBounds(x, rowBounds.y(), size, size);
    }

    static void render(GuiGraphics guiGraphics, ResourceLocation icon, LayoutBounds buttonBounds, int mouseX, int mouseY, boolean focused, boolean active) {
        boolean hovered = active && buttonBounds.contains(mouseX, mouseY);
        guiGraphics.fill(buttonBounds.x(), buttonBounds.y(), buttonBounds.getLimitX(), buttonBounds.getLimitY(), active ? (hovered ? -1610612736 : 0x66000000) : 0x33000000);
        if (focused && BaseWidget.isKeyboardFocusVisible()) {
            BaseWidget.border(guiGraphics, buttonBounds.x(), buttonBounds.y(), buttonBounds.getLimitX(), buttonBounds.getLimitY(), -1);
        }
        int iconX = buttonBounds.getCenterX() - 5;
        int iconY = buttonBounds.getCenterY() - 5;
        if (!active) {
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(icon, iconX, iconY, 0.0f, 0.0f, 10, 10, 10, 10);
        RenderSystem.disableBlend();
        if (!active) {
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

