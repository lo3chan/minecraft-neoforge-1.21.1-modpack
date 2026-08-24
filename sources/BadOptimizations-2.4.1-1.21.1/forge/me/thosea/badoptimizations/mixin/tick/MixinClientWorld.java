package forge.me.thosea.badoptimizations.mixin.tick;

import forge.me.thosea.badoptimizations.config.Config;
import forge.me.thosea.badoptimizations.hook.CacheHooks;
import forge.me.thosea.badoptimizations.interfaces.BiomeSkyColorGetter;
import forge.me.thosea.badoptimizations.utils.CommonColorFactors;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientLevel.class})
public abstract class MixinClientWorld extends Level {
   @Shadow
   @Final
   private Minecraft minecraft;
   private final BiomeSkyColorGetter bo$biomeColors = BiomeSkyColorGetter.of(this.getBiomeManager());
   private final CommonColorFactors bo$commonFactors = CommonColorFactors.SKY_COLOR;
   private Vec3 bo$skyColorCache;
   private int bo$lastBiomeColor = -2147483648;
   private Vec3 bo$biomeColorVector = Vec3.ZERO;

   @Inject(
      method = {"getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetSkyColor(Vec3 cameraPos, float tickDelta, CallbackInfoReturnable<Vec3> cir) {
      if (this.bo$skyColorCache != null && this.minecraft.player != null) {
         CommonColorFactors.tick();
         if (this.bo$commonFactors.didTickChange()) {
            if (this.bo$isBiomeDirty(cameraPos.subtract(2.0, 2.0, 2.0).scale(0.25))) {
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

   private boolean bo$isBiomeDirty(Vec3 pos) {
      int x = Mth.floor(pos.x);
      int y = Mth.floor(pos.y);
      int z = Mth.floor(pos.z);
      int color = this.bo$biomeColors.get(x - 2, y - 2, z - 2);
      if (this.bo$lastBiomeColor != color) {
         this.bo$lastBiomeColor = color;
         this.bo$biomeColorVector = Vec3.fromRGB24(color);
         return true;
      } else {
         return this.bo$biomeColors.get(x + 3, y + 3, z + 3) != color;
      }
   }

   @Shadow
   public abstract int getSkyFlashTime();

   @Shadow
   public abstract Vec3 getSkyColor(Vec3 var1, float var2);

   private Vec3 bo$calcSkyColor(float delta) {
      float angle = Mth.cos(this.getTimeOfDay(1.0F) * 6.2831855F) * 2.0F + 0.5F;
      angle = Mth.clamp(angle, 0.0F, 1.0F);
      double x = this.bo$biomeColorVector.x * angle;
      double y = this.bo$biomeColorVector.y * angle;
      double z = this.bo$biomeColorVector.z * angle;
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

      return new Vec3(x, y, z);
   }

   @Inject(
      method = {"getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"},
      at = {@At("RETURN")}
   )
   private void afterGetSkyColor(Vec3 cameraPos, float tickDelta, CallbackInfoReturnable<Vec3> cir) {
      this.bo$skyColorCache = (Vec3)cir.getReturnValue();
   }

   protected MixinClientWorld(
      WritableLevelData properties,
      ResourceKey<Level> registryRef,
      RegistryAccess registryManager,
      Holder<DimensionType> dimensionEntry,
      Supplier<ProfilerFiller> profiler,
      boolean isClient,
      boolean debugWorld,
      long biomeAccess,
      int maxChainedNeighborUpdates
   ) {
      super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
   }
}
