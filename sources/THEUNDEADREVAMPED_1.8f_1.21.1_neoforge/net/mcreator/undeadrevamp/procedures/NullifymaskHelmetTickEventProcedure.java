package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NullifymaskHelmetTickEventProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MobEffects.WITHER) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WITHER);
         }

         if (entity instanceof LivingEntity _livEnt2 && _livEnt2.hasEffect(MobEffects.POISON) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.POISON);
         }

         if (entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(MobEffects.WEAKNESS) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.WEAKNESS);
         }

         if (entity instanceof LivingEntity _livEnt6 && _livEnt6.hasEffect(UndeadRevamp2ModMobEffects.GOOED) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.GOOED);
         }

         if (entity instanceof LivingEntity _livEnt8 && _livEnt8.hasEffect(MobEffects.DARKNESS) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.DARKNESS);
         }

         if (entity instanceof LivingEntity _livEnt10 && _livEnt10.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
         }

         if (entity instanceof LivingEntity _livEnt12 && _livEnt12.hasEffect(MobEffects.DIG_SLOWDOWN) && entity instanceof LivingEntity _entity) {
            _entity.removeEffect(MobEffects.DIG_SLOWDOWN);
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entity != entityiterator) {
               if (entityiterator instanceof LivingEntity _livEnt15
                  && _livEnt15.hasEffect(MobEffects.INVISIBILITY)
                  && entityiterator instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.INVISIBILITY);
               }

               if (entityiterator instanceof LivingEntity _livEnt17
                  && _livEnt17.hasEffect(MobEffects.MOVEMENT_SPEED)
                  && entityiterator instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.MOVEMENT_SPEED);
               }

               if (entityiterator instanceof LivingEntity _livEnt19
                  && _livEnt19.hasEffect(MobEffects.DAMAGE_BOOST)
                  && entityiterator instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_BOOST);
               }

               if (entityiterator instanceof LivingEntity _livEnt21
                  && _livEnt21.hasEffect(MobEffects.DAMAGE_RESISTANCE)
                  && entityiterator instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
               }

               if (entityiterator instanceof LivingEntity _livEnt23
                  && _livEnt23.hasEffect(MobEffects.FIRE_RESISTANCE)
                  && entityiterator instanceof LivingEntity _entity) {
                  _entity.removeEffect(MobEffects.FIRE_RESISTANCE);
               }
            }
         }
      }
   }
}
