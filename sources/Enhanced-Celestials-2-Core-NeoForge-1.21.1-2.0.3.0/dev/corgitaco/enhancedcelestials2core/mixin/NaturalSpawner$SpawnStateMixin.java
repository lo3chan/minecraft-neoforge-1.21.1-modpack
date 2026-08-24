package dev.corgitaco.enhancedcelestials2core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner.SpawnState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({SpawnState.class})
public class NaturalSpawner$SpawnStateMixin {
   @Shadow
   @Final
   private LocalMobCapCalculator localMobCapCalculator;

   @ModifyExpressionValue(
      method = {"canSpawnForCategory"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/MobCategory;getMaxInstancesPerChunk()I"
      )}
   )
   private int applyLunarSpawnCapMultiplier(int original, MobCategory entityClassification, ChunkPos chunkPos) {
      ServerLevel level = this.localMobCapCalculator.chunkMap.level;
      return EnhancedCelestials.lunarForecastWorldData(level)
         .map(data -> (int)(original * data.currentLunarEvent().getSpawnMultiplierForMonsterCategory(entityClassification)))
         .orElse(original);
   }
}
