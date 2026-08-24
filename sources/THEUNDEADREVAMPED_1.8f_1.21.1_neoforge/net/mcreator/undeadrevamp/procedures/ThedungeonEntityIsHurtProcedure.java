package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.ThedungeonEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThedungeonEntityIsHurtProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 15.0F) {
            if (Math.random() < 0.5) {
               if (entity instanceof ThedungeonEntity) {
                  ((ThedungeonEntity)entity).setAnimation("hurt");
               }
            } else if (entity instanceof ThedungeonEntity) {
               ((ThedungeonEntity)entity).setAnimation("hurt2");
            }
         }

         if (entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:parry")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            world.addParticle(ParticleTypes.FLASH, x, y, z, 0.0, 1.0, 0.0);
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator != entity) {
                  entityiterator.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 1.25 * -1.2,
                        (Math.sin(Math.toRadians(0.0F - entityiterator.getXRot())) + 0.55) * 1.1,
                        Math.cos(Math.toRadians(entityiterator.getYRot())) * 1.25 * -1.38
                     )
                  );
               }
            }
         }
      }
   }
}
