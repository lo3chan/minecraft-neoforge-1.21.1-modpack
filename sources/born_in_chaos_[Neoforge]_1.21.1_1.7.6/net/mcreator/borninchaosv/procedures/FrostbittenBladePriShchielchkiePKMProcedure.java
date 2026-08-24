package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FrostbittenBladePriShchielchkiePKMProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (!(entity instanceof Player _plrCldCheck1 && _plrCldCheck1.getCooldowns().isOnCooldown(itemstack.getItem()))) {
            if (entity instanceof Player _player) {
               _player.getCooldowns().addCooldown(itemstack.getItem(), 240);
            }

            if (world instanceof ServerLevel _level) {
               itemstack.hurtAndBreak(1, _level, null, _stkprov -> {});
            }

            if (!world.isClientSide()) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.hurt_freeze")),
                        SoundSource.NEUTRAL,
                        0.8F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_blow")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:krampus_blow")),
                        SoundSource.NEUTRAL,
                        0.6F,
                        1.0F,
                        false
                     );
                  }
               }
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.SNOWCLOUD.get(), entity.getX(), entity.getY() + 0.2, entity.getZ(), 25, 1.4, 0.1, 1.4, 0.1
               );
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
                  entity.getX(),
                  entity.getY() + 1.5,
                  entity.getZ(),
                  14,
                  1.3,
                  0.8,
                  1.3,
                  0.2
               );
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
                  entity.getX(),
                  entity.getY() + 1.5,
                  entity.getZ(),
                  17,
                  1.4,
                  0.8,
                  1.4,
                  0.2
               );
            }

            if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()) {
               if (entity instanceof LivingEntity _entity) {
                  _entity.swing(InteractionHand.MAIN_HAND, true);
               }
            } else if ((entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getItem() == itemstack.getItem()
               && entity instanceof LivingEntity _entity) {
               _entity.swing(InteractionHand.OFF_HAND, true);
            }

            if ((entity instanceof LivingEntity _entGetArmorxxx ? _entGetArmorxxx.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_HELMET.get()
               && (entity instanceof LivingEntity _entGetArmorxx ? _entGetArmorxx.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_CHESTPLATE.get()
               && (entity instanceof LivingEntity _entGetArmorx ? _entGetArmorx.getItemBySlot(EquipmentSlot.LEGS) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_LEGGINGS.get()
               && (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY).getItem()
                  == BornInChaosV1ModItems.NIGHTMARE_MANTLEOFTHE_NIGHT_BOOTS.get()) {
               Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.75), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiterator != entity
                     && !(entityiterator instanceof TamableAnimal _tamIsTamedBy && entity instanceof LivingEntity _livEntx && _tamIsTamedBy.isOwnedBy(_livEntx))
                     )
                   {
                     if (world instanceof ServerLevel _levelxx) {
                        _levelxx.sendParticles(
                           (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
                           entityiterator.getX(),
                           entityiterator.getY() + 1.4,
                           entityiterator.getZ(),
                           7,
                           0.25,
                           0.25,
                           0.25,
                           0.1
                        );
                     }

                     if (world instanceof ServerLevel _levelxx) {
                        _levelxx.sendParticles(
                           (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
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

                     entityiterator.hurt(
                        new DamageSource(world.holderOrThrow(DamageTypes.FREEZE), entity),
                        (
                              entityiterator instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           + 4
                     );
                     if ((
                           entityiterator instanceof LivingEntity _livEntxxx && _livEntxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                              ? _livEntxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                              : 0
                        )
                        <= 5) {
                        if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(
                              new MobEffectInstance(
                                 BornInChaosV1ModMobEffects.BONE_CHILLING,
                                 240,
                                 (
                                       entityiterator instanceof LivingEntity _livEntxxxx && _livEntxxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                          ? _livEntxxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                          : 0
                                    )
                                    + 2
                              )
                           );
                        }
                     } else if ((
                              entityiterator instanceof LivingEntity _livEntxxxx && _livEntxxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntxxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           >= 6
                        && entityiterator instanceof LivingEntity _entity
                        && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 240, 6));
                     }
                  }
               }

               return;
            } else {
               Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

               for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.75), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiteratorx != entity
                     && !(
                        entityiteratorx instanceof TamableAnimal _tamIsTamedBy && entity instanceof LivingEntity _livEntx && _tamIsTamedBy.isOwnedBy(_livEntx)
                     )) {
                     if (world instanceof ServerLevel _levelxx) {
                        _levelxx.sendParticles(
                           (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLESNOWFLAKE.get(),
                           entityiteratorx.getX(),
                           entityiteratorx.getY() + 1.4,
                           entityiteratorx.getZ(),
                           7,
                           0.25,
                           0.25,
                           0.25,
                           0.1
                        );
                     }

                     if (world instanceof ServerLevel _levelxx) {
                        _levelxx.sendParticles(
                           (SimpleParticleType)BornInChaosV1ModParticleTypes.WANINGSNOWFLAKE.get(),
                           entityiteratorx.getX(),
                           entityiteratorx.getY() + 1.4,
                           entityiteratorx.getZ(),
                           4,
                           0.25,
                           0.25,
                           0.25,
                           0.1
                        );
                     }

                     entityiteratorx.hurt(
                        new DamageSource(world.holderOrThrow(DamageTypes.FREEZE), entity),
                        (
                              entityiteratorx instanceof LivingEntity _livEntxx && _livEntxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           + 2
                     );
                     if ((
                           entityiteratorx instanceof LivingEntity _livEntxxxx && _livEntxxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                              ? _livEntxxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                              : 0
                        )
                        < 6) {
                        if (entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                           _entity.addEffect(
                              new MobEffectInstance(
                                 BornInChaosV1ModMobEffects.BONE_CHILLING,
                                 240,
                                 (
                                       entityiteratorx instanceof LivingEntity _livEntxxxxx && _livEntxxxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                          ? _livEntxxxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                          : 0
                                    )
                                    + 1
                              )
                           );
                        }
                     } else if ((
                              entityiteratorx instanceof LivingEntity _livEntxxxxx && _livEntxxxxx.hasEffect(BornInChaosV1ModMobEffects.BONE_CHILLING)
                                 ? _livEntxxxxx.getEffect(BornInChaosV1ModMobEffects.BONE_CHILLING).getAmplifier()
                                 : 0
                           )
                           >= 6
                        && entityiteratorx instanceof LivingEntity _entity
                        && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BONE_CHILLING, 240, 6));
                     }
                  }
               }
            }
         }
      }
   }
}
