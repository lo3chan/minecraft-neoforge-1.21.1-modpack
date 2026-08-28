/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.MultiLineLabel
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 */
package net.irisshaders.iris.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class FeatureMissingErrorScreen
extends Screen {
    private final Screen parent;
    private final Component messageTemp;
    private MultiLineLabel message;

    public FeatureMissingErrorScreen(Screen parent, Component title, Component message) {
        super(title);
        this.parent = parent;
        this.messageTemp = message;
    }

    protected void init() {
        super.init();
        this.message = MultiLineLabel.create((Font)this.font, (int)(this.width - 50), (Component[])new Component[]{this.messageTemp});
        this.addRenderableWidget((GuiEventListener)Button.builder((Component)CommonComponents.GUI_BACK, arg -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 - 100, 140, 200, 20).build());
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 90, 0xFFFFFF);
        this.message.renderCentered(guiGraphics, this.width / 2, 110, 9, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}

