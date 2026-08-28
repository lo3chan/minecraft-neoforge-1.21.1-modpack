/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$CreateNarration
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package net.irisshaders.iris.gui.element.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class IrisButton
extends Button {
    private final FloatSupplier alphaSupplier;

    public IrisButton(int pButton0, int pInt1, int pInt2, int pInt3, Component pComponent4, Button.OnPress pButton$OnPress5, Button.CreateNarration pButton$CreateNarration6, FloatSupplier alpha) {
        super(pButton0, pInt1, pInt2, pInt3, pComponent4, pButton$OnPress5, pButton$CreateNarration6);
        this.alphaSupplier = alpha;
    }

    public static Builder iris$builder(Component pComponent0, Button.OnPress pButton$OnPress1, FloatSupplier alpha) {
        return new Builder(pComponent0, pButton$OnPress1, alpha);
    }

    protected void renderWidget(GuiGraphics guiGraphics, int pInt1, int pInt2, float pFloat3) {
        Minecraft lvMinecraft5 = Minecraft.getInstance();
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, this.isHoveredOrFocused() ? this.alphaSupplier.getAsFloat() * 1.8f : this.alphaSupplier.getAsFloat());
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        GuiUtil.bindIrisWidgetsTexture();
        GuiUtil.drawButton(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isHoveredOrFocused(), this.active);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, this.alphaSupplier.getAsFloat());
        int lvInt6 = this.active ? 0xFFFFFF : 0xA0A0A0;
        this.renderString(guiGraphics, lvMinecraft5.font, lvInt6 | Mth.ceil((float)(this.alphaSupplier.getAsFloat() * 255.0f)) << 24);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    static /* synthetic */ Button.CreateNarration access$000() {
        return DEFAULT_NARRATION;
    }

    public static class Builder {
        private final Component message;
        private final Button.OnPress onPress;
        private final FloatSupplier alpha;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private Button.CreateNarration createNarration = IrisButton.access$000();

        public Builder(Component pButton$Builder0, Button.OnPress pButton$OnPress1, FloatSupplier alpha) {
            this.message = pButton$Builder0;
            this.onPress = pButton$OnPress1;
            this.alpha = alpha;
        }

        public Builder pos(int pButton$Builder0, int pInt1) {
            this.x = pButton$Builder0;
            this.y = pInt1;
            return this;
        }

        public Builder width(int pButton$Builder0) {
            this.width = pButton$Builder0;
            return this;
        }

        public Builder size(int pButton$Builder0, int pInt1) {
            this.width = pButton$Builder0;
            this.height = pInt1;
            return this;
        }

        public Builder bounds(int pButton$Builder0, int pInt1, int pInt2, int pInt3) {
            return this.pos(pButton$Builder0, pInt1).size(pInt2, pInt3);
        }

        public Builder tooltip(@Nullable Tooltip pButton$Builder0) {
            this.tooltip = pButton$Builder0;
            return this;
        }

        public Builder createNarration(Button.CreateNarration pButton$Builder0) {
            this.createNarration = pButton$Builder0;
            return this;
        }

        public IrisButton build() {
            IrisButton lvButton1 = new IrisButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.createNarration, this.alpha);
            lvButton1.setTooltip(this.tooltip);
            return lvButton1;
        }
    }
}

