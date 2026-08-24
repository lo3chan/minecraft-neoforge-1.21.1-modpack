package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BriskOnEffectActiveTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.08) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!(entityiterator instanceof ItemEntity)) {
                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 5, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 1.0);
                  }

                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  entityiterator.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 1.25 * -1.1,
                        (Math.sin(Math.toRadians(0.0F - entityiterator.getXRot())) + 0.5) * 1.05,
                        Math.cos(Math.toRadians(entityiterator.getYRot())) * 1.05 * -1.05
                     )
                  );
                  entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.STALAGMITE)), 2.0F);
               }
            }
         }
      }
   }
}
