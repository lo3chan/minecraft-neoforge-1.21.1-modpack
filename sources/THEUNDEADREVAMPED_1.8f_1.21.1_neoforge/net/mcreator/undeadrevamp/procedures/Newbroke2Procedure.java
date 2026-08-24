package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Newbroke2Procedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity immediatesourceentity) {
      if (immediatesourceentity != null) {
         if (!world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(x, y, z), 2.5, 2.5, 2.5), e -> true).isEmpty()) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.3), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!(entityiterator instanceof LivingEntity _livEnt1 && _livEnt1.isBlocking())) {
                  entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FALLING_BLOCK)), 4.0F);
               }
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.SNEEZE, x, y, z, 150, 1.0, 2.0, 1.0, 0.0);
            }

            _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!entityiteratorx.getType().is(EntityTypeTags.UNDEAD)) {
                  if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS, 1000, 0));
                  }

                  if (Math.random() < 0.2 && entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.SLEEPWALKING, 200, 0));
                  }
               }
            }

            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }

            if (world instanceof ServerLevel _levelxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }

            if (world instanceof ServerLevel _levelxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }

            if (world instanceof ServerLevel _levelxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }

            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(Blocks.BEEHIVE.defaultBlockState()));
            if (!immediatesourceentity.level().isClientSide()) {
               immediatesourceentity.discard();
            }
         }
      }
   }
}
