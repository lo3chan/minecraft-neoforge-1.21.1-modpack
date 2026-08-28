/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.toasts.Toast
 *  net.minecraft.client.gui.components.toasts.ToastComponent
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.toasts;

import java.util.Deque;
import me.flashyreese.mods.sodiumextra.client.util.ToastFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ToastComponent.class})
public class MixinToastManager {
    @Shadow
    @Final
    private Deque<Toast> queued;

    @Inject(method={"addToast"}, at={@At(value="HEAD")}, cancellable=true)
    public void goodByeToasts(Toast toast, CallbackInfo ci) {
        if (!ToastFilter.isEnabled(toast)) {
            ci.cancel();
        }
    }

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void removeDisabledQueuedToasts(GuiGraphics guiGraphics, CallbackInfo ci) {
        this.queued.removeIf(toast -> !ToastFilter.isEnabled(toast));
    }
}

