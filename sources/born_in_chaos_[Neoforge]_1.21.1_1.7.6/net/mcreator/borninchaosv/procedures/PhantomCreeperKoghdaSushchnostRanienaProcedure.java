package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class PhantomCreeperKoghdaSushchnostRanienaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PHANTOM_CREEPER_COPY.get())
               .spawn(_level, BlockPos.containing(x - 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(entity.getYRot());
               entityToSpawn.setYBodyRot(entity.getYRot());
               entityToSpawn.setYHeadRot(entity.getYRot());
               entityToSpawn.setXRot(entity.getXRot());
            }
         }

         if (world instanceof ServerLevel _levelx) {
            Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.PHANTOM_CREEPER_COPY.get())
               .spawn(_levelx, BlockPos.containing(x + 1.0, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(entity.getYRot());
               entityToSpawn.setYBodyRot(entity.getYRot());
               entityToSpawn.setYHeadRot(entity.getYRot());
               entityToSpawn.setXRot(entity.getXRot());
            }
         }

         if (!entity.level().isClientSide()) {
            entity.discard();
         }

         if (world instanceof Level _levelxx) {
            if (!_levelxx.isClientSide()) {
               _levelxx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _levelxx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxxx) {
            _levelxxx.sendParticles(ParticleTypes.PORTAL, x, y, z, 20, 0.5, 0.5, 0.5, 1.0);
         }

         if (sourceentity instanceof Player
            && !(
               sourceentity instanceof ServerPlayer _plr10
                  && _plr10.level() instanceof ServerLevel
                  && _plr10.getAdvancements()
                     .getOrStartProgress(_plr10.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:double_trouble")))
                     .isDone()
            )
            && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:double_trouble"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
