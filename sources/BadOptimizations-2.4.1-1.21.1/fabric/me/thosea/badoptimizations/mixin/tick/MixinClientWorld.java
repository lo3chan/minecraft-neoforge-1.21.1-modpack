package fabric.me.thosea.badoptimizations.mixin.tick;

import fabric.me.thosea.badoptimizations.config.Config;
import fabric.me.thosea.badoptimizations.hook.CacheHooks;
import fabric.me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import fabric.me.thosea.badoptimizations.utils.CommonColorFactors;
import java.util.function.Supplier;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2874;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3695;
import net.minecraft.class_5269;
import net.minecraft.class_5321;
import net.minecraft.class_5455;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_638.class})
public abstract class MixinClientWorld extends class_1937 {
   @Shadow
   @Final
   private class_310 field_3729;
   private final BiomeSkyColorGetter bo$biomeColors = BiomeSkyColorGetter.of(this.method_22385());
   private final CommonColorFactors bo$commonFactors = CommonColorFactors.SKY_COLOR;
   private class_243 bo$skyColorCache;
   private int bo$lastBiomeColor = -2147483648;
   private class_243 bo$biomeColorVector = class_243.field_1353;

   @Inject(
      method = {"method_23777(Lnet/minecraft/class_243;F)Lnet/minecraft/class_243;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetSkyColor(class_243 cameraPos, float tickDelta, CallbackInfoReturnable<class_243> cir) {
      if (this.bo$skyColorCache != null && this.field_3729.field_1724 != null) {
         CommonColorFactors.tick();
         if (this.bo$commonFactors.didTickChange()) {
            if (this.bo$isBiomeDirty(cameraPos.method_1023(2.0, 2.0, 2.0).method_1021(0.25))) {
               this.bo$commonFactors.updateLastTime();
               return;
            }

            if (this.bo$commonFactors.isDirty() || this.bo$commonFactors.getTimeDelta() >= Config.skyColorTimeForUpdate || CacheHooks.invokeSkyColor()) {
               this.bo$skyColorCache = this.bo$calcSkyColor(tickDelta);
               this.bo$commonFactors.updateLastTime();
            }
         }

         cir.setReturnValue(this.bo$skyColorCache);
      }
   }

   private boolean bo$isBiomeDirty(class_243 pos) {
      int x = class_3532.method_15357(pos.field_1352);
      int y = class_3532.method_15357(pos.field_1351);
      int z = class_3532.method_15357(pos.field_1350);
      int color = this.bo$biomeColors.get(x - 2, y - 2, z - 2);
      if (this.bo$lastBiomeColor != color) {
         this.bo$lastBiomeColor = color;
         this.bo$biomeColorVector = class_243.method_24457(color);
         return true;
      } else {
         return this.bo$biomeColors.get(x + 3, y + 3, z + 3) != color;
      }
   }

   @Shadow
   public abstract int method_23789();

   @Shadow
   public abstract class_243 method_23777(class_243 var1, float var2);

   private class_243 bo$calcSkyColor(float delta) {
      float angle = class_3532.method_15362(this.method_30274(1.0F) * 6.2831855F) * 2.0F + 0.5F;
      angle = class_3532.method_15363(angle, 0.0F, 1.0F);
      double x = this.bo$biomeColorVector.field_1352 * angle;
      double y = this.bo$biomeColorVector.field_1351 * angle;
      double z = this.bo$biomeColorVector.field_1350 * angle;
      if (CommonColorFactors.rainGradientMultiplier > 0.0F) {
         double color = (x * 0.30000001192092896 + y * 0.5899999737739563 + z * 0.10999999940395355) * 0.6000000238418579;
         x = x * CommonColorFactors.rainGradientMultiplier + color * (1.0 - CommonColorFactors.rainGradientMultiplier);
         y = y * CommonColorFactors.rainGradientMultiplier + color * (1.0 - CommonColorFactors.rainGradientMultiplier);
         z = z * CommonColorFactors.rainGradientMultiplier + color * (1.0 - CommonColorFactors.rainGradientMultiplier);
      }

      if (CommonColorFactors.thunderGradientMultiplier > 0.0F) {
         double color = (x * 0.30000001192092896 + y * 0.5899999737739563 + z * 0.10999999940395355) * 0.20000000298023224;
         x = x * CommonColorFactors.thunderGradientMultiplier + color * (1.0 - CommonColorFactors.thunderGradientMultiplier);
         y = y * CommonColorFactors.thunderGradientMultiplier + color * (1.0 - CommonColorFactors.thunderGradientMultiplier);
         z = z * CommonColorFactors.thunderGradientMultiplier + color * (1.0 - CommonColorFactors.thunderGradientMultiplier);
      }

      if (CommonColorFactors.lastLightningTicks > 0) {
         float lightningMultiplier = CommonColorFactors.lastLightningTicks - delta;
         if (lightningMultiplier > 1.0F) {
            lightningMultiplier = 1.0F;
         }

         lightningMultiplier *= 0.45F;
         x = x * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
         y = y * (1.0F - lightningMultiplier) + 0.8F * lightningMultiplier;
         z = z * (1.0F - lightningMultiplier) + lightningMultiplier;
      }

      return new class_243(x, y, z);
   }

   @Inject(
      method = {"method_23777(Lnet/minecraft/class_243;F)Lnet/minecraft/class_243;"},
      at = {@At("RETURN")}
   )
   private void afterGetSkyColor(class_243 cameraPos, float tickDelta, CallbackInfoReturnable<class_243> cir) {
      this.bo$skyColorCache = (class_243)cir.getReturnValue();
   }

   protected MixinClientWorld(
      class_5269 properties,
      class_5321<class_1937> registryRef,
      class_5455 registryManager,
      class_6880<class_2874> dimensionEntry,
      Supplier<class_3695> profiler,
      boolean isClient,
      boolean debugWorld,
      long biomeAccess,
      int maxChainedNeighborUpdates
   ) {
      super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
   }
}
