package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.TherodEntity;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TherodEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (!world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(x, y, z), 7.0, 7.0, 7.0), e -> true).isEmpty()
               == (sourceentity instanceof LivingEntity)
            && (entity instanceof TherodEntity _datEntI ? (Integer)_datEntI.getEntityData().get(TherodEntity.DATA_activatehitbox) : 0) == 1) {
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(Math.toRadians(sourceentity.getYRot() + 180.0F)) * 1.25 * 1.3,
                  (Math.sin(Math.toRadians(0.0F - sourceentity.getXRot())) + 0.5) * 1.5,
                  Math.cos(Math.toRadians(sourceentity.getYRot())) * 1.25 * 1.5
               )
            );
            if (world instanceof ServerLevel _level) {
               _level.sendParticles(ParticleTypes.FLASH, x, y, z, 1, 1.0, 1.0, 1.0, 1.0);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (entity instanceof TherodEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(TherodEntity.DATA_activatehitbox, 0);
            }

            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 12.0F);
            if (sourceentity instanceof ServerPlayer _player) {
               AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:parry"));
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
}
