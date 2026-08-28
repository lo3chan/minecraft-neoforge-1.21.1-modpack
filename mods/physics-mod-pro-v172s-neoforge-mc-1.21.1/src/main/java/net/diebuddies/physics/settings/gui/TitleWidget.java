/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 */
package net.diebuddies.physics.settings.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;

public class TitleWidget
extends AbstractWidget {
    private Screen screen;

    public TitleWidget(Screen screen) {
        super(0, 0, screen.width, screen.height, screen.getTitle());
        this.active = false;
        this.screen = screen;
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.screen.getTitle(), this.width / 2, 15, 0xFFFFFF);
    }

    public void updateWidgetNarration(NarrationElementOutput narration) {
    }
}

