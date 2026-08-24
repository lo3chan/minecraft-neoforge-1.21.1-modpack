package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModEntities;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class PumpkinStaffProProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.PUMPKINSTAFFA.get()
            && world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
            && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 500, 1));
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MR_PUMPKIN_CONTROLLED.get())
                  .spawn(_level, BlockPos.containing(x + 0.5, y + 1.5, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_STAFF_S.get(), x, y + 1.0, z, 12, 0.5, 0.5, 0.5, 0.1);
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(ParticleTypes.POOF, x, y + 1.0, z, 9, 0.5, 0.5, 0.5, 0.1);
            }
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.PUMPKINSTAFFA.get()
            && world.getBlockState(BlockPos.containing(x, y, z)).canOcclude()
            && !world.getBlockState(BlockPos.containing(x, y + 1.0, z)).canOcclude()
            && !(entity instanceof LivingEntity _livEnt14 && _livEnt14.hasEffect(BornInChaosV1ModMobEffects.MAGIC_DEPLETION))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.MAGIC_DEPLETION, 500, 1));
            }

            if (world instanceof ServerLevel _levelxx) {
               Entity entityToSpawn = ((EntityType)BornInChaosV1ModEntities.MR_PUMPKIN_CONTROLLED.get())
                  .spawn(_levelxx, BlockPos.containing(x + 0.5, y + 1.5, z + 0.5), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof Level _levelxxx) {
               if (!_levelxxx.isClientSide()) {
                  _levelxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.glow_squid.squirt")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxxxx) {
               _levelxxxx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.PUMPKIN_STAFF_S.get(), x, y + 1.0, z, 12, 0.5, 0.5, 0.5, 0.1);
            }

            if (world instanceof ServerLevel _levelxxxx) {
               _levelxxxx.sendParticles(ParticleTypes.POOF, x, y + 1.0, z, 9, 0.5, 0.5, 0.5, 0.1);
            }
         }
      }
   }
}
