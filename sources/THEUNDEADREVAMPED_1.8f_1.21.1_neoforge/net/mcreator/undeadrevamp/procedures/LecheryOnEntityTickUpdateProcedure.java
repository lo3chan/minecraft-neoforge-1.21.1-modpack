package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.LecheryEntity;
import net.mcreator.undeadrevamp.entity.WeakspotEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LecheryOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!entity.isAlive() && entity instanceof LecheryEntity animatable) {
            animatable.setTexture("the_lechery_dies");
         }

         if (entity.isAlive()) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof WeakspotEntity && !entity.isVehicle()) {
                  entityiterator.startRiding(entity);
                  UndeadRevamp2Mod.queueServerWork(2, () -> {
                     if (!entityiterator.isPassenger() && !entityiterator.level().isClientSide()) {
                        entityiterator.discard();
                     }
                  });
               }
            }
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.5), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorx instanceof WeakspotEntity && !entityiteratorx.isPassenger() && !entityiteratorx.level().isClientSide()) {
               entityiteratorx.discard();
            }
         }

         if (entity.isInWaterRainOrBubble()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.DROWN)), 3.0F);
            entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
         }
      }
   }
}
