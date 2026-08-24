package dev.corgitaco.enhancedcelestials2core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import java.util.Optional;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BeaconBlockEntity.class})
public class BeaconBlockEntityMixin {
   @ModifyExpressionValue(
      method = {"applyEffects"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
      )}
   )
   private static AABB scaleBeaconArea(AABB original, Level level) {
      Optional<LunarForecast> enhancedCelestialsLunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(level);
      if (enhancedCelestialsLunarForecastWorldData.isEmpty()) {
         return original;
      } else {
         LunarForecast data = enhancedCelestialsLunarForecastWorldData.orElseThrow();
         double beaconRadiusAmplifier = data.currentLunarEvent().beaconRadiusAmplifier();
         return AABB.ofSize(
            original.getCenter(),
            original.getXsize() * beaconRadiusAmplifier,
            original.getYsize() * beaconRadiusAmplifier,
            original.getZsize() * beaconRadiusAmplifier
         );
      }
   }
}
