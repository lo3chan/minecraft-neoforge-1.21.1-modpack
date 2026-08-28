/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package net.diebuddies.mixins.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.PhysicsDebugOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Gui.class})
public class MixinGui {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private PhysicsDebugOverlay physicsmod$debugOverlay;

    @Inject(at={@At(value="TAIL")}, method={"render"})
    public void physicsmod$renderDebugInfo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if (this.physicsmod$debugOverlay == null) {
            this.physicsmod$debugOverlay = new PhysicsDebugOverlay(this.minecraft);
        }
        if (ConfigClient.renderPhysicsDebugOverlay) {
            this.physicsmod$debugOverlay.render(guiGraphics);
        }
    }
}

