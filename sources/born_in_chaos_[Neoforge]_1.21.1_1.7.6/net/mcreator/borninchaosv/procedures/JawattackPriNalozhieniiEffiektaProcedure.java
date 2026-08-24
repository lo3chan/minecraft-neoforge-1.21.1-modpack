package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class JawattackPriNalozhieniiEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         Entity entityToSpawn = EntityType.EVOKER_FANGS.spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
         }
      }

      if (world instanceof ServerLevel _levelx) {
         Entity entityToSpawn = EntityType.EVOKER_FANGS.spawn(_levelx, BlockPos.containing(x + 1.0, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
         }
      }

      if (world instanceof ServerLevel _levelxx) {
         Entity entityToSpawn = EntityType.EVOKER_FANGS.spawn(_levelxx, BlockPos.containing(x - 1.0, y, z), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
         }
      }

      if (world instanceof ServerLevel _levelxxx) {
         Entity entityToSpawn = EntityType.EVOKER_FANGS.spawn(_levelxxx, BlockPos.containing(x, y, z - 1.0), MobSpawnType.MOB_SUMMONED);
         if (entityToSpawn != null) {
         }
      }

      if (world instanceof Level _levelxxxx) {
         if (!_levelxxxx.isClientSide()) {
            _levelxxxx.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
               SoundSource.NEUTRAL,
               0.6F,
               1.0F
            );
         } else {
            _levelxxxx.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.evoker.prepare_attack")),
               SoundSource.NEUTRAL,
               0.6F,
               1.0F,
               false
            );
         }
      }
   }
}
