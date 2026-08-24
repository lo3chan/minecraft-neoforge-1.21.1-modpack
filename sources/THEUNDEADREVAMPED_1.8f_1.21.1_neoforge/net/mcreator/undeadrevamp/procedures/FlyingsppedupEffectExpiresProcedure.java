package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.ThehunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FlyingsppedupEffectExpiresProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof ThehunterEntity) {
            ((ThehunterEntity)entity).setAnimation("empty");
         }

         entity.setShiftKeyDown(false);
         if (entity instanceof ThehunterEntity && Math.random() < 0.3) {
            if (entity instanceof ThehunterEntity) {
               ((ThehunterEntity)entity).setAnimation("turbine");
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     3.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:hunter_fly")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     3.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
                  entity.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entity.getYRot() + 180.0F)) * 1.25 * 1.5,
                        (Math.sin(Math.toRadians(0.0F - entity.getXRot())) + 0.5) * -1.2,
                        Math.cos(Math.toRadians(entity.getYRot())) * 1.25 * 1.5
                     )
                  );
               }
            }
         }
      }
   }
}
