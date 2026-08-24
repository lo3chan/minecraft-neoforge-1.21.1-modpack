package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.BigsuckerEntity;
import net.mcreator.undeadrevamp.entity.SuckerEntity;
import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BrokentankOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof ThehunterEntity || entity instanceof SuckerEntity || entity instanceof BigsuckerEntity) {
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(Math.toRadians(entity.getYRot() + 180.0F))
                     * 1.5
                     * (
                        0.2
                           + (
                                 entity instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP)
                                    ? _livEntxx.getEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP).getAmplifier()
                                    : 0
                              )
                              / 5
                     ),
                  (Math.sin(Math.toRadians(0.0F - entity.getXRot())) + 0.0)
                     * (
                        0.2
                           + (
                                 entity instanceof LivingEntity _livEntx && _livEntx.hasEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP)
                                    ? _livEntx.getEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP).getAmplifier()
                                    : 0
                              )
                              / 5
                     ),
                  Math.cos(Math.toRadians(entity.getYRot()))
                     * 2.0
                     * (
                        0.1
                           + (
                                 entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP)
                                    ? _livEnt.getEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP).getAmplifier()
                                    : 0
                              )
                              / 10
                     )
               )
            );
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator) {
                  if (entity instanceof LivingEntity _entity) {
                     _entity.removeEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP);
                  }

                  entity.getPersistentData().putDouble("gaszz_spead", 120.0);
               }
            }

            if (entity.onGround() && entity instanceof ThehunterEntity) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.removeEffect(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP);
               }

               entity.getPersistentData().putDouble("gaszz_spead", 120.0);
            }
         }

         if (entity instanceof ThehunterEntity && entity.isAlive() && entity instanceof ThehunterEntity) {
            ((ThehunterEntity)entity).setAnimation("fly");
         }
      }
   }
}
