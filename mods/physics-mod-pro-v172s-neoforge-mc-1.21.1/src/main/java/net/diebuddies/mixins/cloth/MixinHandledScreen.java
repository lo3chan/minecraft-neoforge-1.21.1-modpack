/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractContainerScreen.class})
public class MixinHandledScreen {
    @Inject(at={@At(value="HEAD")}, method={"render"})
    public void physicsmod$renderHead(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
        PhysicsMod.hudRendering = true;
    }

    @Inject(at={@At(value="TAIL")}, method={"render"})
    public void physicsmod$renderTail(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
        PhysicsMod.hudRendering = false;
    }
}

