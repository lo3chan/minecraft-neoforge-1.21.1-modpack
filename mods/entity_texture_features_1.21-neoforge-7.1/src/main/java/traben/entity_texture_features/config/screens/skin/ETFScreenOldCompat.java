/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.Button$OnPress
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FormattedCharSequence
 */
package traben.entity_texture_features.config.screens.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.mixin.mixins.accessor.TooltipAccessor;
import traben.tconfig.gui.TConfigScreen;

public abstract class ETFScreenOldCompat
extends TConfigScreen {
    protected ETFScreenOldCompat(String title, Screen parent, boolean showBackButton) {
        super(title, parent, showBackButton);
    }

    public static void renderGUITexture(GuiGraphics context, ResourceLocation texture, double x1, double y1, double x2, double y2) {
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex((float)x1, (float)y2, 0.0f).setUv(0.0f, 1.0f).setColor(255, 255, 255, 255);
        bufferBuilder.addVertex((float)x2, (float)y2, 0.0f).setUv(1.0f, 1.0f).setColor(255, 255, 255, 255);
        bufferBuilder.addVertex((float)x2, (float)y1, 0.0f).setUv(1.0f, 0.0f).setColor(255, 255, 255, 255);
        bufferBuilder.addVertex((float)x1, (float)y1, 0.0f).setUv(0.0f, 0.0f).setColor(255, 255, 255, 255);
        BufferUploader.drawWithShader((MeshData)bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static String booleanAsOnOff(boolean bool) {
        return CommonComponents.optionStatus((boolean)bool).getString();
    }

    public Button getETFButton(int x, int y, int width, int height, Component buttonText, Button.OnPress onPress) {
        return this.getETFButton(x, y, width, height, buttonText, onPress, Component.nullToEmpty((String)""));
    }

    public Button getETFButton(int x, int y, int width, int height, Component buttonText, Button.OnPress onPress, Component toolTipText) {
        int nudgeLeftEdge;
        if (width > 384) {
            nudgeLeftEdge = (width - 384) / 2;
            width = 384;
        } else {
            nudgeLeftEdge = 0;
        }
        boolean tooltipIsEmpty = toolTipText.getString().isBlank();
        if (tooltipIsEmpty) {
            return Button.builder((Component)buttonText, (Button.OnPress)onPress).bounds(x + nudgeLeftEdge, y, width, height).build();
        }
        Tooltip bob = Tooltip.create((Component)toolTipText);
        if (!ETF.isThisModLoaded("adaptive-tooltips")) {
            String[] strings = toolTipText.getString().split("\n");
            ArrayList<FormattedCharSequence> texts = new ArrayList<FormattedCharSequence>();
            for (String str : strings) {
                texts.add(Component.nullToEmpty((String)str).getVisualOrderText());
            }
            ((TooltipAccessor)bob).setCachedTooltip(texts);
        }
        return Button.builder((Component)buttonText, (Button.OnPress)onPress).bounds(x + nudgeLeftEdge, y, width, height).tooltip(bob).build();
    }
}

