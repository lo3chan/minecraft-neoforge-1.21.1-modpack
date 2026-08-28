/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Math
 */
package net.diebuddies.physics.settings.ux;

import net.diebuddies.math.Math;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.minecraft.client.gui.GuiGraphics;

public class MouseParallaxAnimator
extends Animator {
    protected float baseX;
    protected float baseY;
    protected float baseWidth;
    protected float baseHeight;
    protected float currentMouseX;
    protected float currentMouseY;
    protected float oldMouseX;
    protected float oldMouseY;
    protected float nextMouseX = Float.MAX_VALUE;
    protected float nextMouseY = Float.MAX_VALUE;
    protected boolean showBorder;
    protected float parallaxMultiplier;
    protected float mouseSmoothness;

    public MouseParallaxAnimator(float parallaxMultiplier, boolean showBorder) {
        this.showBorder = showBorder;
        this.parallaxMultiplier = parallaxMultiplier;
        this.mouseSmoothness = 0.15f;
    }

    public MouseParallaxAnimator() {
        this(0.1f, false);
    }

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        if (this.nextMouseX == Float.MAX_VALUE) {
            this.currentMouseX = Math.clamp((float)mouseX, 0.0f, this.baseWidth);
            this.currentMouseY = Math.clamp((float)mouseY, 0.0f, this.baseHeight);
            this.oldMouseX = this.currentMouseX;
            this.oldMouseY = this.currentMouseY;
        }
        this.nextMouseX = Math.clamp((float)mouseX, 0.0f, this.baseWidth);
        this.nextMouseY = Math.clamp((float)mouseY, 0.0f, this.baseHeight);
        Animatable image = animatable;
        float lMouseX = org.joml.Math.lerp((float)this.oldMouseX, (float)this.currentMouseX, (float)renderPercent);
        float lMouseY = org.joml.Math.lerp((float)this.oldMouseY, (float)this.currentMouseY, (float)renderPercent);
        float offsetX = -Math.clamp(lMouseX / (this.baseWidth + this.baseX) * 2.0f - 1.0f, -1.0f, 1.0f);
        float offsetY = -Math.clamp(lMouseY / (this.baseHeight + this.baseY) * 2.0f - 1.0f, -1.0f, 1.0f);
        float offsetMultiplierX = this.parallaxMultiplier * this.baseWidth;
        float offsetMultiplierY = this.parallaxMultiplier * this.baseHeight;
        if (!this.showBorder) {
            float zoomX = this.baseX - offsetMultiplierX;
            float zoomY = this.baseY - offsetMultiplierY;
            float zoomWidth = this.baseWidth + offsetMultiplierX * 2.0f;
            float zoomHeight = this.baseHeight + offsetMultiplierY * 2.0f;
            image.setAnimX(zoomX + offsetX * offsetMultiplierX);
            image.setAnimY(zoomY + offsetY * offsetMultiplierY);
            image.setAnimWidth(zoomWidth);
            image.setAnimHeight(zoomHeight);
        } else {
            image.setAnimX(this.baseX + offsetX * offsetMultiplierX);
            image.setAnimY(this.baseY + offsetY * offsetMultiplierY);
        }
        return false;
    }

    @Override
    public void tick(Animatable animatable) {
        this.updatePositions();
        if (this.nextMouseX != Float.MAX_VALUE) {
            this.currentMouseX = org.joml.Math.lerp((float)this.currentMouseX, (float)this.nextMouseX, (float)this.mouseSmoothness);
            this.currentMouseY = org.joml.Math.lerp((float)this.currentMouseY, (float)this.nextMouseY, (float)this.mouseSmoothness);
        }
    }

    @Override
    public void init(Animatable animatable) {
        this.baseX = animatable.getAnimX();
        this.baseY = animatable.getAnimY();
        this.baseWidth = animatable.getAnimWidth();
        this.baseHeight = animatable.getAnimHeight();
        this.currentMouseX = this.baseWidth * 0.5f + this.baseX;
        this.currentMouseY = this.baseHeight * 0.5f + this.baseY;
        this.updatePositions();
    }

    private void updatePositions() {
        this.oldMouseX = this.currentMouseX;
        this.oldMouseY = this.currentMouseY;
    }

    public float getParallaxMultiplier() {
        return this.parallaxMultiplier;
    }

    public MouseParallaxAnimator setParallaxMultiplier(float parallaxMultiplier) {
        this.parallaxMultiplier = parallaxMultiplier;
        return this;
    }

    public float getMouseSmoothness() {
        return this.mouseSmoothness;
    }

    public MouseParallaxAnimator setMouseSmoothness(float mouseSmoothness) {
        this.mouseSmoothness = mouseSmoothness;
        return this;
    }
}

