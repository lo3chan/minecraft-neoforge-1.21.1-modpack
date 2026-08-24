package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.entity.ThebidyEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SuckerOnInitialEntitySpawnProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.getPersistentData().putBoolean("fall", false);
         if ((Boolean)MobsabilityConfiguration.SUCK_CH.get() && Math.random() < (Double)MobsabilityConfiguration.SUCK_TEEM.get() / 100.0) {
            if (Math.random() < (Double)MobsabilityConfiguration.SUC_BIDY.get() / 100.0) {
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEBIDY.get())
                     .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                  }
               }
            } else if (!entity.level().isClientSide() && entity.getServer() != null) {
               entity.getServer()
                  .getCommands()
                  .performPrefixedCommand(
                     new CommandSourceStack(
                        CommandSource.NULL,
                        entity.position(),
                        entity.getRotationVector(),
                        entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null,
                        4,
                        entity.getName().getString(),
                        entity.getDisplayName(),
                        entity.level().getServer(),
                        entity
                     ),
                     "/summon zombie ~ ~ ~ {IsBaby:1}"
                  );
            }
         }

         UndeadRevamp2Mod.queueServerWork(
            2,
            () -> {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (world.getEntitiesOfClass(ThebidyEntity.class, AABB.ofSize(new Vec3(x, y, z), 8.0, 8.0, 8.0), e -> true).stream().sorted((new Object() {
                     Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                        return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                     }
                  }).compareDistOf(x, y, z)).findFirst().orElse(null) == entityiterator) {
                     entityiterator.startRiding(entity);
                  }

                  if (world.getEntitiesOfClass(Zombie.class, AABB.ofSize(new Vec3(x, y, z), 8.0, 8.0, 8.0), e -> true).stream().sorted((new Object() {
                     Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                        return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                     }
                  }).compareDistOf(x, y, z)).findFirst().orElse(null) == entityiterator && entityiterator instanceof LivingEntity _livEnt11 && _livEnt11.isBaby()
                     )
                   {
                     entityiterator.startRiding(entity);
                  }
               }
            }
         );
      }
   }
}
