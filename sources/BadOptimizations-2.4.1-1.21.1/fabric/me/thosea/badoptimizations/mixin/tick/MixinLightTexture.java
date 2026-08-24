package fabric.me.thosea.badoptimizations.mixin.tick;

import fabric.me.thosea.badoptimizations.config.Config;
import fabric.me.thosea.badoptimizations.hook.CacheHooks;
import fabric.me.thosea.badoptimizations.mixin.accessors.GameRendererAccessor;
import fabric.me.thosea.badoptimizations.mixin.accessors.PlayerAccessor;
import fabric.me.thosea.badoptimizations.utils.CommonColorFactors;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_310;
import net.minecraft.class_5294;
import net.minecraft.class_757;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_765.class})
public abstract class MixinLightTexture {
   @Shadow
   @Final
   private class_310 field_4137;
   private final CommonColorFactors bo$commonFactors = CommonColorFactors.LIGHTMAP;
   private double bo$lastGamma;
   private class_5294 bo$lastDimension;
   private boolean bo$lastNightVision;
   private boolean bo$lastConduitPower;
   private float bo$previousSkyDarkness;
   private GameRendererAccessor bo$gameRendererAccessor;

   @Inject(
      method = {"<init>(Lnet/minecraft/class_757;Lnet/minecraft/class_310;)V"},
      at = {@At("TAIL")}
   )
   private void onInit(class_757 renderer, class_310 client, CallbackInfo ci) {
      this.bo$gameRendererAccessor = (GameRendererAccessor)renderer;
   }

   private boolean bo$isDirty() {
      if (this.bo$commonFactors.getTimeDelta() >= Config.lightmapTimeForUpdate) {
         return true;
      } else if (this.field_4137.field_1724.method_5869() && ((PlayerAccessor)this.field_4137.field_1724).bo$underwaterVisibilityTicks() < 600) {
         return true;
      } else {
         class_1293 nightVision = this.field_4137.field_1724.method_6112(class_1294.field_5925);
         boolean hasNightVision = nightVision != null;
         if (this.bo$lastNightVision != hasNightVision) {
            this.bo$lastNightVision = hasNightVision;
            return true;
         } else if (nightVision != null && nightVision.method_48557(200)) {
            return true;
         } else if (this.field_4137.field_1724.method_6059(class_1294.field_38092)) {
            return true;
         } else {
            boolean conduitPower = this.field_4137.field_1724.method_6059(class_1294.field_5927);
            if (this.bo$lastConduitPower != conduitPower) {
               this.bo$lastConduitPower = conduitPower;
               return true;
            } else {
               class_5294 dimension = this.field_4137.field_1687.method_28103();
               if (this.bo$lastDimension != dimension) {
                  this.bo$lastDimension = dimension;
                  return true;
               } else {
                  float skyDarkness = this.bo$gameRendererAccessor.bo$getSkyDarkness();
                  if (this.bo$previousSkyDarkness != skyDarkness) {
                     this.bo$previousSkyDarkness = skyDarkness;
                     return true;
                  } else {
                     double gamma = (Double)this.field_4137.field_1690.method_42473().method_41753();
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
      method = {"method_3314()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onTick(CallbackInfo ci) {
      if (this.field_4137.field_1724 != null) {
         CommonColorFactors.tick();
         if (this.bo$commonFactors.didTickChange() && this.bo$commonFactors.isDirty() | this.bo$isDirty()) {
            this.bo$commonFactors.updateLastTime();
         } else {
            ci.cancel();
         }
      }
   }
}
