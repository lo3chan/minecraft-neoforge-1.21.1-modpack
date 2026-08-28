/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  org.lwjgl.glfw.GLFW
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package me.flashyreese.mods.sodiumextra.mixin.adaptive_sync;

import com.mojang.blaze3d.platform.Window;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.config.SodiumExtraGameOptions;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={Window.class})
public class MixinWindow {
    @Unique
    private static boolean sodiumExtra$usesAdaptiveSync() {
        return SodiumExtraClientMod.options().extraSettings.useAdaptiveSync && SodiumExtraGameOptions.VerticalSyncOption.isAdaptiveSyncSupported();
    }

    @Redirect(method={"updateVsync"}, at=@At(value="INVOKE", target="Lorg/lwjgl/glfw/GLFW;glfwSwapInterval(I)V", remap=false))
    private void setSwapInterval(int interval) {
        if (interval > 0 && MixinWindow.sodiumExtra$usesAdaptiveSync()) {
            GLFW.glfwSwapInterval((int)-1);
            return;
        }
        GLFW.glfwSwapInterval((int)interval);
    }
}

