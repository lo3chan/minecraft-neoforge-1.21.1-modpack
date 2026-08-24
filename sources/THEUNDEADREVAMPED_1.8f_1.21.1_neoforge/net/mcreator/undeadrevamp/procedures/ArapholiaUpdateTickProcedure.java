package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArapholiaUpdateTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (world.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(x, y, z), 8.0, 8.0, 8.0), e -> true).stream().sorted((new Object() {
               Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                  return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
               }
            }).compareDistOf(x, y, z)).findFirst().orElse(null) == entityiterator
            && entityiterator instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 500, 0));
         }
      }
   }
}
