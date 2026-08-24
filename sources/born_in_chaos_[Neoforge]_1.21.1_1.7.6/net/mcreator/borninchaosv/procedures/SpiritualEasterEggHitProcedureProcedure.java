package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SpiritualEasterEggHitProcedureProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:chaos_spirit_haunt")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:chaos_spirit_haunt")),
               SoundSource.NEUTRAL,
               1.0F,
               1.0F,
               false
            );
         }
      }

      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof Monster) {
            entityiterator.getPersistentData().putDouble("randomgain", Mth.nextInt(RandomSource.create(), 1, 3));
            if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(
                  new MobEffectInstance(
                     BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER,
                     2000,
                     (
                           entityiterator instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER)
                              ? _livEnt.getEffect(BornInChaosV1ModMobEffects.EXPERIENCE_CONTAINER).getAmplifier()
                              : 0
                        )
                        + 1,
                     false,
                     false
                  )
               );
            }
         }
      }

      _center = new Vec3(x, y, z);

      for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiteratorx instanceof Monster) {
            if (entityiteratorx.getPersistentData().getDouble("randomgain") == 1.0) {
               if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.OBSESSION, 2000, 0, false, false));
               }
            } else if (entityiteratorx.getPersistentData().getDouble("randomgain") == 3.0) {
               if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.ICE_BARRIER, 1000, 0));
               }
            } else if (entityiteratorx.getPersistentData().getDouble("randomgain") == 2.0
               && entityiteratorx instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.CURSED_MARK, 100, 0));
            }
         }
      }
   }
}
