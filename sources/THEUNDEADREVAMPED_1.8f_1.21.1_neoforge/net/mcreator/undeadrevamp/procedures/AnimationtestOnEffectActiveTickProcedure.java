package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.mcreator.undeadrevamp.entity.ThebeartamerEntity;
import net.mcreator.undeadrevamp.entity.ThehorrorsdecoysEntity;
import net.mcreator.undeadrevamp.entity.ThespitterEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class AnimationtestOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof ThespitterEntity) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 30, false, false));
            }

            entity.setShiftKeyDown(true);
         }

         if (entity instanceof ThehorrorsdecoysEntity) {
            entity.setShiftKeyDown(true);
            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.ASH, x, y, z, 25, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0E-5);
            }

            if (entity instanceof ThehorrorsdecoysEntity animatable) {
               animatable.setTexture("horrorsdecoysdecaying");
            }

            if (Math.random() < 0.045 && world instanceof ServerLevel _level) {
               _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 1));
            }
         }

         if (entity instanceof ThebeartamerEntity) {
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
            entity.setDeltaMovement(new Vec3(0.0, -5.0, 0.0));
         }

         if (entity instanceof LecheryEntity
            && entity.isAlive()
            && (
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST)
                     ? _livEnt.getEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST).getDuration()
                     : 0
               )
               == 50
            && !(entity instanceof LivingEntity _livEnt17 && _livEnt17.hasEffect(UndeadRevamp2ModMobEffects.FULMINATION))) {
            if ((
                     entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
                        ? _livEntx.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier()
                        : 0
                  )
                  != 30
               && world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.tnt.primed")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.tnt.primed")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.WEAKSPOT.get())
                  .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
               }
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 30, false, false));
            }

            if (entity instanceof LecheryEntity) {
               ((LecheryEntity)entity).setAnimation("recharged");
            }

            UndeadRevamp2Mod.queueServerWork(55, () -> {
               if (entity.isAlive()) {
                  if (entity instanceof LivingEntity _entityxxx && !_entityxxx.level().isClientSide()) {
                     _entityxxx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FULMINATION, 250, 30, false, false));
                  }

                  if (entity instanceof LivingEntity _entityxx) {
                     _entityxx.removeEffect(UndeadRevamp2ModMobEffects.ANIMATIONTEST);
                  }

                  if (entity instanceof LivingEntity _entityx) {
                     _entityx.setHealth(entity instanceof LivingEntity _livEntxx ? _livEntxx.getMaxHealth() : -1.0F);
                  }
               }
            });
         }
      }
   }
}
