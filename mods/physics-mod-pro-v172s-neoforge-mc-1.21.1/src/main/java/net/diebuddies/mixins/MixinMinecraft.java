/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Overlay
 *  net.minecraft.client.gui.screens.Screen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public class MixinMinecraft {
    @Shadow
    private Overlay overlay;
    @Unique
    private boolean firstVerification = true;
    @Unique
    private volatile boolean failedVerification;

    @Inject(at={@At(value="HEAD")}, method={"clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V"})
    public void clearLevel(Screen screen, CallbackInfo info) {
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            mod.getPhysicsWorld().destroy();
        }
        PhysicsMod.getInstances().clear();
    }

    @Inject(at={@At(value="HEAD")}, method={"getFramerateLimit"}, cancellable=true)
    private void getFramerateLimit(CallbackInfoReturnable<Integer> info) {
        Minecraft mc = (Minecraft)this;
        if (mc.level == null && (mc.screen != null || this.overlay != null) && mc.screen instanceof PhysicsSettingsScreen) {
            info.setReturnValue((Object)120);
        }
    }
}

