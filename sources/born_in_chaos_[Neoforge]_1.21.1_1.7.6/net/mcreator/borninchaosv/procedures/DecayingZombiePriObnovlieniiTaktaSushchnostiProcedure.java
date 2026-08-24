package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class DecayingZombiePriObnovlieniiTaktaSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) <= 5.0F) {
            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.SPLASHOFFLESH.get(), x, y + 0.5, z, 15, 0.5, 0.5, 0.5, 0.2);
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLESHSPLASH.get(), x, y + 1.0, z, 5, 0.3, 0.3, 0.3, 0.1);
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.DECREPIT_SKELETON.get())
                  .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
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

            for (int index0 = 0; index0 < 2; index0++) {
               if (world instanceof ServerLevel _levelx) {
                  ItemEntity entityToSpawn = new ItemEntity(_levelx, x, y, z, new ItemStack(Items.ROTTEN_FLESH));
                  entityToSpawn.setPickUpDelay(10);
                  _levelx.addFreshEntity(entityToSpawn);
               }
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.HOSTILE,
                     0.7F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stomach_open")),
                     SoundSource.HOSTILE,
                     0.7F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.9F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.slime_block.break")),
                     SoundSource.NEUTRAL,
                     0.9F,
                     0.9F,
                     false
                  );
               }
            }
         }

         if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 1.0, z))) {
            if (world instanceof Level _lvl11
               && _lvl11.isDay()
               && !world.getLevelData().isRaining()
               && !world.getLevelData().isThundering()
               && !entity.isInWaterRainOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if (entity.isInWaterRainOrBubble()) {
               entity.clearFire();
            }
         }

         if (entity instanceof LivingEntity _livEnt20
            && _livEnt20.hasEffect(BornInChaosV1ModMobEffects.INFESTATIONOF_FLIES)
            && (
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.INFESTATIONOF_FLIES)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.INFESTATIONOF_FLIES).getDuration()
                     : 0
               )
               <= 20) {
            if (world instanceof ServerLevel _levelxxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.SWARMER.get())
                  .spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
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

            if (world instanceof Level _levelxxxx) {
               if (!_levelxxxx.isClientSide()) {
                  _levelxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.converted_to_drowned")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     0.9F
                  );
               } else {
                  _levelxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.converted_to_drowned")),
                     SoundSource.NEUTRAL,
                     0.8F,
                     0.9F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxxxxx) {
               _levelxxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.FLI.get(), x, y + 1.0, z, 14, 0.5, 0.5, 0.5, 0.2);
            }
         }
      }
   }
}
