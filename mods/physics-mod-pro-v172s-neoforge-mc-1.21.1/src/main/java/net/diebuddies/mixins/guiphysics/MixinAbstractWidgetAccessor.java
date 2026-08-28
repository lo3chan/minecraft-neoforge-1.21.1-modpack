/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.WidgetTooltipHolder
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package net.diebuddies.mixins.guiphysics;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={AbstractWidget.class})
public interface MixinAbstractWidgetAccessor {
    @Accessor(value="isHovered")
    public boolean getIsHovered();

    @Accessor(value="isHovered")
    public void setIsHovered(boolean var1);

    @Accessor(value="focused")
    public void setFocused(boolean var1);

    @Accessor(value="tooltip")
    public WidgetTooltipHolder getTooltipHolder();
}

