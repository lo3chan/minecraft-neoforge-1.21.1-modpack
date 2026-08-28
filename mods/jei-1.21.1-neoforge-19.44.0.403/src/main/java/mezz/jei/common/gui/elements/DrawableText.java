/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 */
package mezz.jei.common.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class DrawableText
implements IDrawable {
    private final String text;
    private final int width;
    private final int height;
    private final int color;

    public DrawableText(String text, int width, int height, int color) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        int textCenterX = xOffset + this.width / 2;
        int textCenterY = yOffset + this.height / 2 - 3;
        int stringCenter = fontRenderer.width(this.text) / 2;
        guiGraphics.drawString(fontRenderer, this.text, textCenterX - stringCenter, textCenterY, this.color);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

