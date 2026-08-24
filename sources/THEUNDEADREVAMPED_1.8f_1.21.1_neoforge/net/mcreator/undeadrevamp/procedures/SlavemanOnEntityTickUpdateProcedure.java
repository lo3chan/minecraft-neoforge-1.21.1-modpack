package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SlavemanOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (!entity.isPassenger() && !entityiterator.isVehicle() && entityiterator instanceof ThehunterEntity && entityiterator.isAlive()) {
               entity.startRiding(entityiterator);
               if (entityiterator instanceof TamableAnimal _toTame && entity instanceof Player _owner) {
                  _toTame.tame(_owner);
               }
            }
         }

         _center = new Vec3(x, y, z);

         for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.5), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorx.isVehicle()
               && entityiteratorx instanceof ThehunterEntity
               && entityiteratorx instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, false, false));
            }
         }

         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            Vec3 _centerx = new Vec3(x, y, z);

            for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(25.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorxx instanceof ThehunterEntity
                  && entityiteratorxx instanceof Mob _entity
                  && (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity _ent) {
                  _entity.setTarget(_ent);
               }
            }
         }

         if (entity.isAlive()) {
            _center = new Vec3(x, y, z);

            for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorxxx instanceof ThehunterEntity && entityiteratorxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.HOARDMANE, 80, 0, false, false));
               }
            }
         }

         if (!entity.onGround() && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0, false, false));
         }

         if (entity.getVehicle() instanceof ThehunterEntity && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 60, 0, false, false));
         }
      }
   }
}
