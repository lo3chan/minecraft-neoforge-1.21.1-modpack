package forge.me.thosea.badoptimizations.mixin.tick;

import forge.me.thosea.badoptimizations.config.Config;
import forge.me.thosea.badoptimizations.hook.CacheHooks;
import forge.me.thosea.badoptimizations.mixin.accessors.GameRendererAccessor;
import forge.me.thosea.badoptimizations.mixin.accessors.PlayerAccessor;
import forge.me.thosea.badoptimizations.utils.CommonColorFactors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LightTexture.class})
public abstract class MixinLightTexture {
   @Shadow
   @Final
   private Minecraft minecraft;
   private final CommonColorFactors bo$commonFactors = CommonColorFactors.LIGHTMAP;
   private double bo$lastGamma;
   private DimensionSpecialEffects bo$lastDimension;
   private boolean bo$lastNightVision;
   private boolean bo$lastConduitPower;
   private float bo$previousSkyDarkness;
   private GameRendererAccessor bo$gameRendererAccessor;

   @Inject(
      method = {"<init>(Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/Minecraft;)V"},
      at = {@At("TAIL")}
   )
   private void onInit(GameRenderer renderer, Minecraft client, CallbackInfo ci) {
      this.bo$gameRendererAccessor = (GameRendererAccessor)renderer;
   }

   private boolean bo$isDirty() {
      if (this.bo$commonFactors.getTimeDelta() >= Config.lightmapTimeForUpdate) {
         return true;
      } else if (this.minecraft.player.isUnderWater() && ((PlayerAccessor)this.minecraft.player).bo$underwaterVisibilityTicks() < 600) {
         return true;
      } else {
         MobEffectInstance nightVision = this.minecraft.player.getEffect(MobEffects.NIGHT_VISION);
         boolean hasNightVision = nightVision != null;
         if (this.bo$lastNightVision != hasNightVision) {
            this.bo$lastNightVision = hasNightVision;
            return true;
         } else if (nightVision != null && nightVision.endsWithin(200)) {
            return true;
         } else if (this.minecraft.player.hasEffect(MobEffects.DARKNESS)) {
            return true;
         } else {
            boolean conduitPower = this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER);
            if (this.bo$lastConduitPower != conduitPower) {
               this.bo$lastConduitPower = conduitPower;
               return true;
            } else {
               DimensionSpecialEffects dimension = this.minecraft.level.effects();
               if (this.bo$lastDimension != dimension) {
                  this.bo$lastDimension = dimension;
                  return true;
               } else {
                  float skyDarkness = this.bo$gameRendererAccessor.bo$getSkyDarkness();
                  if (this.bo$previousSkyDarkness != skyDarkness) {
                     this.bo$previousSkyDarkness = skyDarkness;
                     return true;
                  } else {
                     double gamma = (Double)this.minecraft.options.gamma().get();
                     if (this.bo$lastGamma != gamma) {
                        this.bo$lastGamma = gamma;
                        return true;
                     } else {
                        return CacheHooks.invokeLightmap();
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onTick(CallbackInfo ci) {
      if (this.minecraft.player != null) {
         CommonColorFactors.tick();
         if (this.bo$commonFactors.didTickChange() && this.bo$commonFactors.isDirty() | this.bo$isDirty()) {
            this.bo$commonFactors.updateLastTime();
         } else {
            ci.cancel();
         }
      }
   }
}
