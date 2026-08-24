package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.BigsuckerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SuckerOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity && entity.isAlive()) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator != entity) {
                  if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator) {
                     if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 10, 0, false, false));
                     }
                  } else if (!(entity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                     && entity instanceof LivingEntity _entity
                     && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.FLYINGSPPEDUP, 10, 0, false, false));
                  }
               }
            }
         }

         if (entity.getPersistentData().getBoolean("fall") && !entity.isAlive()) {
            entity.setDeltaMovement(new Vec3(0.0, -2.0, 0.0));
         }

         if (entity instanceof BigsuckerEntity && Math.random() < 0.008 && world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:flap")),
                  SoundSource.NEUTRAL,
                  4.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x, y, z, (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:flap")), SoundSource.NEUTRAL, 4.0F, 1.0F, false
               );
            }
         }
      }
   }
}
