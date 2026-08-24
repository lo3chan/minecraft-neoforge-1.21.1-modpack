package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.KrampusEntity;
import net.mcreator.borninchaosv.entity.KrampusHenchmanEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SnowStormKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.SNOWCLOUD.get(), entity.getX(), entity.getY() + 0.2, entity.getZ(), 3, 1.4, 0.1, 1.4, 0.1
            );
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
               entity.getX(),
               entity.getY() + 2.0,
               entity.getZ(),
               3,
               1.0,
               0.6,
               1.0,
               0.2
            );
         }

         if (world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
               entity.getX(),
               entity.getY() + 2.0,
               entity.getZ(),
               1,
               1.0,
               0.6,
               1.0,
               0.1
            );
         }

         if (entity.getPersistentData().getDouble("freezing") == 0.0) {
            entity.getPersistentData().putDouble("freezing", 40.0);
         } else {
            entity.getPersistentData().putDouble("freezing", entity.getPersistentData().getDouble("freezing") - 1.0);
         }

         if (entity.getPersistentData().getDouble("freezing") == 0.0) {
            Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.75), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!(entityiterator instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(BornInChaosV1ModMobEffects.SNOW_STORM))
                  && !(entityiterator instanceof KrampusEntity)
                  && !(entityiterator instanceof KrampusHenchmanEntity)
                  && !(entityiterator instanceof ItemEntity)
                  && !(entityiterator instanceof TamableAnimal _tamIsTamedBy && entity instanceof LivingEntity _livEnt && _tamIsTamedBy.isOwnedBy(_livEnt))) {
                  if (entityiterator instanceof LivingEntity _livEnt25 && _livEnt25.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)) {
                     if (entityiterator instanceof LivingEntity _livEnt29
                        && _livEnt29.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           == 0) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 100, 1));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 1.0F);
                     } else if (entityiterator instanceof LivingEntity _livEnt34
                        && _livEnt34.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           == 1) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 200, 2));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 1.0F);
                     } else if (entityiterator instanceof LivingEntity _livEnt39
                        && _livEnt39.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           == 2) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 140, 3));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 2.0F);
                     } else if (entityiterator instanceof LivingEntity _livEnt44
                        && _livEnt44.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           == 3) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 100, 4));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 2.0F);
                     } else if (entityiterator instanceof LivingEntity _livEnt49
                        && _livEnt49.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           == 4) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 100, 5));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 3.0F);
                     } else if (entityiterator instanceof LivingEntity _livEnt54
                        && _livEnt54.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           == 5) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 80, 6));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 3.0F);
                     } else if (entityiterator instanceof LivingEntity _livEnt59
                        && _livEnt59.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                        && (
                              entityiterator instanceof LivingEntity _livEntx && _livEntx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           >= 6) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 80, 6));
                        }

                        entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 4.0F);
                     }
                  } else {
                     if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 80, 0));
                     }

                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FREEZE)), 1.0F);
                  }

                  if (!world.isClientSide() && world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(entityiterator.getX(), entityiterator.getY() + 1.4, entityiterator.getZ()),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                           SoundSource.NEUTRAL,
                           0.8F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           entityiterator.getX(),
                           entityiterator.getY() + 1.4,
                           entityiterator.getZ(),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                           SoundSource.NEUTRAL,
                           0.8F,
                           1.0F,
                           false
                        );
                     }
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(
                        (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
                        entityiterator.getX(),
                        entityiterator.getY() + 1.4,
                        entityiterator.getZ(),
                        4,
                        0.25,
                        0.25,
                        0.25,
                        0.1
                     );
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles(
                        (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
                        entityiterator.getX(),
                        entityiterator.getY() + 1.4,
                        entityiterator.getZ(),
                        2,
                        0.25,
                        0.25,
                        0.25,
                        0.1
                     );
                  }
               }
            }
         }

         if (entity instanceof Player
            && (
               world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.WATER
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.KELP_PLANT
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.SEAGRASS
                  || world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getBlock() == Blocks.TALL_SEAGRASS
            )) {
            world.setBlock(BlockPos.containing(x, y - 1.0, z), Blocks.FROSTED_ICE.defaultBlockState(), 3);
         }
      }
   }
}
