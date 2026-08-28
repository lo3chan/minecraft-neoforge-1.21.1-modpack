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
 *  it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FastColor$ARGB32
 *  org.joml.Math
 *  org.joml.Matrix4f
 */
package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.diebuddies.math.Math;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.diebuddies.physics.settings.ux.BaseColors;
import net.diebuddies.physics.settings.ux.GUIResources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

public class ScrollAnimator
extends Animator {
    private Object2FloatMap<Animatable> baseOffsets;
    private float scrollOffset = 0.0f;
    private float oldScrollOffset;
    private float currentScrollOffset;
    private float minOffset;
    private float maxOffset;
    private float scrollSpeed = 40.0f;
    private float animationSpeed = 0.5f;
    private float screenHeight;
    private float arrowXPos;
    private float time;
    private int hoveredColor = BaseColors.HIGHLIGHT_COLOR;

    public ScrollAnimator(float minOffset, float maxOffset, float arrowXPos, float screenHeight) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.screenHeight = screenHeight;
        this.arrowXPos = arrowXPos;
        this.baseOffsets = new Object2FloatLinkedOpenHashMap();
    }

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        this.baseOffsets.putIfAbsent((Object)animatable, animatable.getAnimY());
        float baseY = this.baseOffsets.getFloat((Object)animatable);
        animatable.setAnimY(baseY + org.joml.Math.lerp((float)this.oldScrollOffset, (float)this.currentScrollOffset, (float)renderPercent));
        Matrix4f pose = guiGraphics.pose().last().pose();
        if (animatable == this.baseOffsets.keySet().iterator().next()) {
            int color;
            BufferBuilder bufferBuilder;
            boolean shadowOffset = true;
            this.time += delta;
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)GUIResources.ARROW);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            float width = 12.0f;
            float height = 12.0f;
            float x = this.arrowXPos - width * 0.5f + (float)shadowOffset;
            float y = this.screenHeight * 0.95f - height * 0.5f + (float)java.lang.Math.sin((double)this.time * 6.0) + (float)shadowOffset;
            float depth = 130.0f;
            if (this.scrollOffset - 0.01f >= this.minOffset) {
                bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                color = FastColor.ARGB32.color((int)255, (int)0, (int)0, (int)0);
                Animator.drawRect(bufferBuilder, pose, x, y, width, height, depth, 0.0f, 1.0f, 0.0f, 1.0f, color);
                color = FastColor.ARGB32.color((int)255, (int)255, (int)255, (int)255);
                if (this.isOverBottomArrow(mouseX, mouseY)) {
                    color = this.hoveredColor;
                }
                Animator.drawRect(bufferBuilder, pose, x -= (float)shadowOffset, y -= (float)shadowOffset, width, height, depth, 0.0f, 1.0f, 0.0f, 1.0f, color);
                BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
            }
            if (this.scrollOffset + 0.01f <= this.maxOffset) {
                bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                color = FastColor.ARGB32.color((int)255, (int)0, (int)0, (int)0);
                x = this.arrowXPos - width * 0.5f + (float)shadowOffset;
                y = this.screenHeight * 0.05f - height / 2.0f + (float)java.lang.Math.cos((double)this.time * 6.0) + (float)shadowOffset;
                Animator.drawRect(bufferBuilder, pose, x, y, width, height, depth, 0.0f, 1.0f, 1.0f, 0.0f, color);
                color = FastColor.ARGB32.color((int)255, (int)255, (int)255, (int)255);
                if (this.isOverTopArrow(mouseX, mouseY)) {
                    color = this.hoveredColor;
                }
                Animator.drawRect(bufferBuilder, pose, x -= (float)shadowOffset, y -= (float)shadowOffset, width, height, depth, 0.0f, 1.0f, 1.0f, 0.0f, color);
                BufferUploader.drawWithShader((MeshData)bufferBuilder.build());
            }
            RenderSystem.disableBlend();
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseKey) {
        if (mouseKey == 0) {
            if (this.isOverBottomArrow(mouseX, mouseY)) {
                this.scroll(-2.0f);
                return true;
            }
            if (this.isOverTopArrow(mouseX, mouseY)) {
                this.scroll(2.0f);
                return true;
            }
        }
        return false;
    }

    private boolean isOverBottomArrow(double mouseX, double mouseY) {
        float width = 24.0f;
        float height = 16.0f;
        float x = this.arrowXPos - width * 0.5f;
        float y = this.screenHeight * 0.95f - height * 0.5f + (float)java.lang.Math.sin((double)this.time * 6.0);
        return mouseX >= (double)x && mouseY >= (double)y && mouseX < (double)(x + width) && mouseY < (double)(y + height);
    }

    private boolean isOverTopArrow(double mouseX, double mouseY) {
        float width = 24.0f;
        float height = 16.0f;
        float x = this.arrowXPos - width * 0.5f;
        float y = this.screenHeight * 0.05f - height * 0.5f + (float)java.lang.Math.sin((double)this.time * 6.0);
        return mouseX >= (double)x && mouseY >= (double)y && mouseX < (double)(x + width) && mouseY < (double)(y + height);
    }

    @Override
    public void tick(Animatable animatable) {
        if (this.baseOffsets.size() == 0 || animatable == this.baseOffsets.keySet().iterator().next()) {
            this.oldScrollOffset = this.currentScrollOffset;
            this.currentScrollOffset = org.joml.Math.lerp((float)this.currentScrollOffset, (float)this.scrollOffset, (float)this.animationSpeed);
        }
    }

    public void scroll(float scroll) {
        this.scrollOffset = Math.clamp(this.scrollOffset + scroll * this.scrollSpeed, this.minOffset, this.maxOffset);
    }

    public float getScrollSpeed() {
        return this.scrollSpeed;
    }

    public void setScrollSpeed(float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setMinOffset(float minOffset) {
        this.minOffset = minOffset;
    }

    public void setMaxOffset(float maxOffset) {
        this.maxOffset = maxOffset;
    }

    public float getMinOffset() {
        return this.minOffset;
    }

    public float getMaxOffset() {
        return this.maxOffset;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public float getAnimationSpeed() {
        return this.animationSpeed;
    }
}

