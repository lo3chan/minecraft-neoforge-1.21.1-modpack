package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.NeocrorinesEntity;
import net.mcreator.undeadrevamp.entity.ThedungeonEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThedungeonOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (Math.random() < 0.15
            && entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)
            && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.SMOKE, x, y, z, 15, 5.0, 1.0, 5.0, 0.12);
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.5), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator
               && !(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.elder_guardian.curse")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.elder_guardian.curse")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.UNDEADSTUNS, 1000, 0, false, false));
               }

               if (entity instanceof ThedungeonEntity) {
                  ((ThedungeonEntity)entity).setAnimation("strike");
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt9 && _livEnt9.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS)) {
            Vec3 _centerx = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(5.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiteratorx) {
                  if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 30, 2));
                  }

                  if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0));
                  }

                  entity.lookAt(Anchor.EYES, new Vec3(entityiteratorx.getX(), entityiteratorx.getY(), entityiteratorx.getZ()));
                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        entityiteratorx.getX(),
                        entityiteratorx.getY(),
                        entityiteratorx.getZ(),
                        50,
                        entityiteratorx.getBbWidth(),
                        entityiteratorx.getBbHeight(),
                        entityiteratorx.getBbWidth(),
                        0.0
                     );
                  }
               }
            }
         }

         _center = new Vec3(x, y, z);

         for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(32.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiteratorxx instanceof Monster
               && !(entityiteratorxx instanceof LivingEntity _livEnt27 && _livEnt27.hasEffect(UndeadRevamp2ModMobEffects.DUNGEONBUFF))
               && !(entityiteratorxx instanceof NeocrorinesEntity)
               && entityiteratorxx instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.DUNGEONBUFF, 100, 0));
            }
         }
      }
   }
}
