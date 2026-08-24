package dev.corgitaco.enhancedcelestials2core.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarForecast;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Optional;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.LocalMobCapCalculator.MobCounts;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LocalMobCapCalculator.class})
public class LocalMobCapCalculatorMixin {
   @Shadow
   @Final
   public ChunkMap chunkMap;

   @WrapOperation(
      method = {"canSpawn"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/LocalMobCapCalculator$MobCounts;canSpawn(Lnet/minecraft/world/entity/MobCategory;)Z"
      )}
   )
   private boolean useLunarEventMobCap(MobCounts instance, MobCategory mobCategory, Operation<Boolean> original) {
      ServerLevel level = this.chunkMap.level;
      Optional<LunarForecast> lunarForecastWorldData = EnhancedCelestials.lunarForecastWorldData(level);
      if (lunarForecastWorldData.isPresent()) {
         LunarForecast data = lunarForecastWorldData.orElseThrow();
         Object2IntMap<MobCategory> counts = instance.counts;
         int currentCount = counts.getOrDefault(mobCategory, 0);
         return currentCount < mobCategory.getMaxInstancesPerChunk() * data.currentLunarEvent().getSpawnMultiplierForMonsterCategory(mobCategory);
      } else {
         return (Boolean)original.call(new Object[]{instance, mobCategory});
      }
   }
}
