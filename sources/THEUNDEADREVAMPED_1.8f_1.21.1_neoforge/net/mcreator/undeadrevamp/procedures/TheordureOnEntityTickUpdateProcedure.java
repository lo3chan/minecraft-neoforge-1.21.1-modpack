package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.TheordureEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class TheordureOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (entity.isAlive()) {
               entity.setDeltaMovement(new Vec3(0.0, 10.0, 0.0));
            }
         } else if (entity.isAlive() && !entity.level().isClientSide()) {
            entity.discard();
         }

         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && entity.getPersistentData().getDouble("crackle") == 0.0
            && Math.random() < 0.7) {
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:oduresking")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:oduresking")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F,
                     false
                  );
               }
            }

            entity.getPersistentData().putDouble("crackle", 1.0);
            if (world instanceof ServerLevel _levelx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                  .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, -1.0, 0.0);
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                  .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, -1.0, 0.0);
               }
            }

            if (world instanceof ServerLevel _levelxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                  .spawn(_levelxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, -1.0, 0.0);
               }
            }

            if (world instanceof ServerLevel _levelxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                  .spawn(_levelxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, -1.0, 0.0);
               }
            }

            if (world instanceof ServerLevel _levelxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get())
                  .spawn(_levelxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setDeltaMovement(0.0, -1.0, 0.0);
               }
            }

            if (entity instanceof TheordureEntity) {
               ((TheordureEntity)entity).setAnimation("shaking");
            }

            UndeadRevamp2Mod.queueServerWork(100, () -> entity.getPersistentData().putDouble("crackle", 0.0));
         }
      }
   }
}
