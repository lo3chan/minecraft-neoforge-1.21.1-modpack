/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 */
package net.diebuddies.physics.settings.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class LabelOption
extends LegacyOption {
    private String value;
    private int inactiveColor = 0xA0A0A0;
    public LabelComponent label;

    public LabelOption(String value) {
        super(value);
        this.value = value;
    }

    @Override
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        this.label = new LabelComponent(x, y, width, 20, (Component)Component.literal((String)this.value));
        return this.label;
    }

    public void setInactiveColor(int inactiveColor) {
        this.inactiveColor = inactiveColor;
    }

    public class LabelComponent
    extends AbstractWidget {
        public LabelComponent(int x, int y, int width, int height, Component component) {
            super(x, y, width, height, component);
        }

        public void updateWidgetNarration(NarrationElementOutput narration) {
        }

        public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
            PoseStack matrices = guiGraphics.pose();
            matrices.pushPose();
            matrices.translate(0.0f, 0.0f, -100.0f);
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int color = this.active ? 0xFFFFFF : LabelOption.this.inactiveColor;
            guiGraphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color | Mth.ceil((float)(this.alpha * 255.0f)) << 24);
            matrices.popPose();
        }
    }
}

