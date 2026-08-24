package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.TheimmortalEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class InvisiimmortalOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.getPersistentData().getBoolean("choosen")) {
            if (entity.getPersistentData().getDouble("rare") == 1.0) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator) {
                     if (!entity.level().isClientSide()) {
                        entity.discard();
                     }

                     if (world instanceof ServerLevel _level) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEIMMORTAL.get())
                           .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     entity.getPersistentData().putBoolean("choosen", true);
                  }
               }

               UndeadRevamp2Mod.queueServerWork(
                  5,
                  () -> {
                     Vec3 _centerx = new Vec3(x, y, z);

                     for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(10.0), e -> true)
                        .stream()
                        .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                        .toList()) {
                        if (entityiteratorx instanceof TheimmortalEntity) {
                           entityiteratorx.getPersistentData().putDouble("decored", 1.0);
                        }
                     }
                  }
               );
            } else {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiteratorx) {
                     if (!entity.level().isClientSide()) {
                        entity.discard();
                     }

                     if (world instanceof ServerLevel _levelx) {
                        Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEIMMORTAL.get())
                           .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                        if (entityToSpawn != null) {
                           entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                        }
                     }

                     entity.getPersistentData().putBoolean("choosen", true);
                  }
               }
            }
         }

         if (entity.getPersistentData().getDouble("flee") != 1.0
            && world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 2.0, 2.0, 2.0), e -> true).isEmpty()
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && Math.random() < 0.25) {
            world.levelEvent(2001, BlockPos.containing(x, y - 0.1, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))));
         }
      }
   }
}
