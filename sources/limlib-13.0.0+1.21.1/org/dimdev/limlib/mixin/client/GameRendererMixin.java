package org.dimdev.limlib.mixin.client;

import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.impl.shader.PostProcesser;
import org.dimdev.limlib.impl.shader.PostProcesserManager;
import org.dimdev.limlib.post.PostEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Unique
   private final Function<ResourceLocation, PostProcesser> memoizedShaders = Util.memoize(PostProcesserManager.INSTANCE::find);

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V",
         shift = Shift.AFTER
      )}
   )
   private void limlib$render(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
      LookupGrabber.<PostEffect>snatchFromLevel(this.minecraft.level, LimLibRegistryKeys.POST_EFFECT)
         .filter(PostEffect::shouldRender)
         .ifPresent(postEffect -> {
            EffectRenderers.PostEffectRenderer<PostEffect> renderer = EffectRenderers.get(postEffect);
            if (renderer != null) {
               renderer.beforeRender(postEffect);
               this.memoizedShaders.apply(postEffect.getShaderLocation()).render(deltaTracker.getGameTimeDeltaPartialTick(false));
            }
         });
   }
}
