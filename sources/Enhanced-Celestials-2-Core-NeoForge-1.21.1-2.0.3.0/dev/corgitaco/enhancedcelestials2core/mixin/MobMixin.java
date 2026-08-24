package dev.corgitaco.enhancedcelestials2core.mixin;

import dev.corgitaco.enhancedcelestials2core.EnhancedCelestials;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Mob.class})
public abstract class MobMixin {
   @Inject(
      method = {"finalizeSpawn"},
      at = {@At("TAIL")}
   )
   private void lunarEquipOnSpawn(
      ServerLevelAccessor level,
      DifficultyInstance difficulty,
      MobSpawnType spawnType,
      @Nullable SpawnGroupData spawnGroupData,
      CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      Mob mob = (Mob)this;
      EnhancedCelestials.lunarForecastWorldData(mob.level()).ifPresent(data -> data.currentLunarEvent().equipMobOnSpawn(mob));
   }
}
