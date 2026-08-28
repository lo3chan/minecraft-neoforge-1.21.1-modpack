/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.gui.GuiGraphics
 */
package net.diebuddies.physics.settings.ux;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.minecraft.client.gui.GuiGraphics;

public class FocusSelector
extends Animator {
    private Set<Animatable> list = new ObjectOpenHashSet();
    private Animatable lastFocus = null;

    @Override
    public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
        MixinAbstractWidgetAccessor accessor;
        boolean hovered = false;
        if (animatable instanceof MixinAbstractWidgetAccessor) {
            accessor = (MixinAbstractWidgetAccessor)((Object)animatable);
            hovered = accessor.getIsHovered();
        }
        if (hovered) {
            Animatable animatable2;
            if (this.lastFocus != null && this.lastFocus != animatable && (animatable2 = this.lastFocus) instanceof MixinAbstractWidgetAccessor) {
                accessor = (MixinAbstractWidgetAccessor)((Object)animatable2);
                accessor.setFocused(false);
            }
            if (animatable instanceof MixinAbstractWidgetAccessor) {
                accessor = (MixinAbstractWidgetAccessor)((Object)animatable);
                accessor.setFocused(true);
                this.lastFocus = animatable;
            }
        }
        return super.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
    }

    @Override
    public void init(Animatable animatable) {
        super.init(animatable);
        this.list.add(animatable);
    }

    public void deselectAll() {
        for (Animatable animatable : this.list) {
            if (!(animatable instanceof MixinAbstractWidgetAccessor)) continue;
            MixinAbstractWidgetAccessor accessor = (MixinAbstractWidgetAccessor)((Object)animatable);
            accessor.setFocused(false);
        }
    }

    public Animatable getFocusedElement() {
        return this.lastFocus;
    }
}

