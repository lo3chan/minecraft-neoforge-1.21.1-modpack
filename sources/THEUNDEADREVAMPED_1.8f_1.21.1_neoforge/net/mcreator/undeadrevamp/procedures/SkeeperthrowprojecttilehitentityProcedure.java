package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlocks;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SkeeperthrowprojecttilehitentityProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity != entity) {
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

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!entityiterator.getType().is(EntityTypeTags.UNDEAD) && entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.TRYPANOSOMIASIS, 1000, 0));
               }
            }

            if (Math.random() < 0.25) {
               _center = new Vec3(x, y, z);

               for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (!entityiteratorx.getType().is(EntityTypeTags.UNDEAD)
                     && entityiteratorx instanceof LivingEntity _entity
                     && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.SLEEPWALKING, 200, 0));
                  }
               }
            }

            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxxxx, BlockPos.containing(x, y + 0.5, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxxxxx, BlockPos.containing(x, y + 0.5, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxxxxxx, BlockPos.containing(x, y + 0.5, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            if (world instanceof ServerLevel _levelxxxxxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get())
                  .spawn(_levelxxxxxxxxx, BlockPos.containing(x, y + 0.5, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }

            world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(((Block)UndeadRevamp2ModBlocks.WOODENNEST.get()).defaultBlockState()));
         }
      }
   }
}
