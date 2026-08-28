/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 */
package net.diebuddies.physics.settings.ux;

import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

public class TooltipRenderer
extends Animator {
    private Renderable renderable;

    public TooltipRenderer(Renderable renderable) {
        this.renderable = renderable;
    }

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        AbstractWidget widget;
        if (animatable instanceof AbstractWidget && (widget = (AbstractWidget)animatable).isHoveredOrFocused()) {
            this.renderable.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
        }
        return false;
    }

    public static interface Renderable {
        public void render(Animatable var1, GuiGraphics var2, int var3, int var4, float var5, float var6);
    }
}

