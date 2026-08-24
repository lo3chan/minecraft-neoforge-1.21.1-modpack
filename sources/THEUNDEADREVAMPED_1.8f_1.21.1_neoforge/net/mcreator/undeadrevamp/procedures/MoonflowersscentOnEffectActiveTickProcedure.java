package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.TheMoonflowerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MoonflowersscentOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(150.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof TheMoonflowerEntity) {
               if ((
                     entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT)
                        ? _livEnt.getEffect(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT).getAmplifier()
                        : 0
                  )
                  >= 1) {
                  if (entityiterator instanceof Mob _entity && entity instanceof LivingEntity _ent) {
                     _entity.setTarget(_ent);
                  }

                  if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 10, 0, false, false));
                  }
               } else if (!entity.getType().is(EntityTypeTags.UNDEAD)) {
                  if (entityiterator instanceof Mob _entity && entity instanceof LivingEntity _ent) {
                     _entity.setTarget(_ent);
                  }

                  if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT, 10, 0, false, false));
                  }
               }
            }
         }
      }
   }
}
