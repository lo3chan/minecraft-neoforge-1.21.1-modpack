/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.DebugScreenOverlay
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.steady_debug_hud;

import java.util.ArrayList;
import java.util.List;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={DebugScreenOverlay.class})
public abstract class MixinDebugScreenOverlay {
    @Unique
    private final List<String> leftTextCache = new ArrayList<String>();
    @Unique
    private final List<String> rightTextCache = new ArrayList<String>();
    @Unique
    private long nextTime = 0L;
    @Unique
    private boolean rebuild = true;

    @Shadow
    protected abstract void renderLines(GuiGraphics var1, List<String> var2, boolean var3);

    @Inject(method={"render"}, at={@At(value="HEAD")})
    public void preRender(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (SodiumExtraClientMod.options().extraSettings.steadyDebugHud) {
            long currentTime = Util.getMillis();
            if (currentTime > this.nextTime) {
                this.rebuild = true;
                this.nextTime = currentTime + (long)SodiumExtraClientMod.options().extraSettings.steadyDebugHudRefreshInterval * 50L;
            } else {
                this.rebuild = false;
            }
        } else {
            this.rebuild = true;
        }
    }

    @Redirect(method={"drawGameInformation"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"))
    public void sodiumExtra$redirectDrawLeftText(DebugScreenOverlay instance, GuiGraphics guiGraphics, List<String> text, boolean left) {
        if (this.rebuild) {
            this.leftTextCache.clear();
            this.leftTextCache.addAll(text);
        }
        this.renderLines(guiGraphics, this.leftTextCache, left);
    }

    @Redirect(method={"drawSystemInformation"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/DebugScreenOverlay;renderLines(Lnet/minecraft/client/gui/GuiGraphics;Ljava/util/List;Z)V"))
    public void sodiumExtra$redirectDrawRightText(DebugScreenOverlay instance, GuiGraphics guiGraphics, List<String> text, boolean left) {
        if (this.rebuild) {
            this.rightTextCache.clear();
            this.rightTextCache.addAll(text);
        }
        this.renderLines(guiGraphics, this.rightTextCache, left);
    }

    @Inject(method={"drawGameInformation"}, at={@At(value="HEAD")}, cancellable=true)
    private void sodiumExtra$drawCachedGameInformation(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!this.rebuild) {
            this.renderLines(guiGraphics, this.leftTextCache, true);
            ci.cancel();
        }
    }

    @Inject(method={"drawSystemInformation"}, at={@At(value="HEAD")}, cancellable=true)
    private void sodiumExtra$drawCachedSystemInformation(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!this.rebuild) {
            this.renderLines(guiGraphics, this.rightTextCache, false);
            ci.cancel();
        }
    }
}

