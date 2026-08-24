package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.ThesmokerEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThesmokerOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof ThesmokerEntity _datEntI ? (Integer)_datEntI.getEntityData().get(ThesmokerEntity.DATA_bubblehp) : 0) > 0) {
            if (Math.random() < 0.03
               && !world.isClientSide()
               && (entity instanceof ThesmokerEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(ThesmokerEntity.DATA_gas_delay) : 0) == 1
               && (entity instanceof ThesmokerEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(ThesmokerEntity.DATA_fume_whezeticks) : 0) == -1
               && entity.isAlive()) {
               if (entity instanceof ThesmokerEntity _datEntSetI) {
                  _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_fume_whezeticks, 39);
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:wheebomb")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:wheebomb")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 10, false, false));
               }

               if (entity instanceof ThesmokerEntity) {
                  ((ThesmokerEntity)entity).setAnimation("snort");
               }

               if (entity instanceof ThesmokerEntity _datEntSetI) {
                  _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_gas_delay, 0);
               }

               UndeadRevamp2Mod.queueServerWork(150, () -> {
                  if (entity instanceof ThesmokerEntity _datEntSetI) {
                     _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_gas_delay, 1);
                  }
               });
            }

            if ((entity instanceof ThesmokerEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(ThesmokerEntity.DATA_fume_whezeticks) : 0) == 0
               && entity.isAlive()) {
               entity.setShiftKeyDown(false);
               if (entity instanceof ThesmokerEntity _datEntSetI) {
                  _datEntSetI.getEntityData().set(ThesmokerEntity.DATA_fume_whezeticks, -1);
               }

               if (!entity.isOnFire()) {
                  Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

                  for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.25), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     if (entityiterator != entity && entityiterator instanceof LivingEntity) {
                        entityiterator.getPersistentData().putDouble("aoe_x", entity.getX() - entityiterator.getX());
                        entityiterator.getPersistentData()
                           .putDouble("aoe_y", entity.getY() + entity.getBbHeight() - (entityiterator.getY() + entityiterator.getBbHeight()));
                        entityiterator.getPersistentData().putDouble("aoe_z", entity.getZ() - entityiterator.getZ());
                        entityiterator.getPersistentData().putDouble("distance", 0.0);
                        UndeadRevamp2Mod.queueServerWork(
                           1,
                           () -> {
                              for (int index0 = 0; index0 < 20; index0++) {
                                 if (world.isEmptyBlock(
                                    BlockPos.containing(
                                       entity.getX()
                                          + entityiterator.getPersistentData().getDouble("aoe_x") * entityiterator.getPersistentData().getDouble("distance"),
                                       entity.getY()
                                          + entity.getBbHeight()
                                          + entityiterator.getPersistentData().getDouble("aoe_y") * entityiterator.getPersistentData().getDouble("distance"),
                                       entity.getZ()
                                          + entityiterator.getPersistentData().getDouble("aoe_z") * entityiterator.getPersistentData().getDouble("distance")
                                    )
                                 )) {
                                    entityiterator.getPersistentData().putBoolean("behind_wall", false);
                                    entityiterator.getPersistentData().putDouble("distance", entityiterator.getPersistentData().getDouble("distance") - 0.05);
                                 } else {
                                    entityiterator.getPersistentData().putBoolean("behind_wall", true);
                                 }

                                 UndeadRevamp2Mod.queueServerWork(
                                    1,
                                    () -> {
                                       if (!entityiterator.getPersistentData().getBoolean("behind_wall")
                                          && !(entityiterator instanceof LivingEntity _livEnt50 && _livEnt50.isBlocking())) {
                                          if (!entityiterator.getType().is(EntityTypeTags.UNDEAD)) {
                                             if (entityiterator instanceof LivingEntity _entityxxxxxx && !_entityxxxxxx.level().isClientSide()) {
                                                _entityxxxxxx.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1, false, false));
                                             }

                                             if (entityiterator instanceof LivingEntity _entityxxxxx && !_entityxxxxx.level().isClientSide()) {
                                                _entityxxxxx.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
                                             }

                                             if (entityiterator instanceof LivingEntity _entityxxxx && !_entityxxxx.level().isClientSide()) {
                                                _entityxxxx.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.TOXICFUMES, 350, 0, false, false));
                                             }
                                          } else {
                                             if (entityiterator instanceof LivingEntity _entityxxx && !_entityxxx.level().isClientSide()) {
                                                _entityxxx.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, false));
                                             }

                                             if (entityiterator instanceof LivingEntity _entityxx && !_entityxx.level().isClientSide()) {
                                                _entityxx.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 150, 1, false, false));
                                             }

                                             if (entityiterator instanceof LivingEntity _entityx && !_entityx.level().isClientSide()) {
                                                _entityx.addEffect(new MobEffectInstance(MobEffects.HARM, 2, 0, false, false));
                                             }
                                          }
                                       }
                                    }
                                 );
                              }
                           }
                        );
                     }
                  }

                  if (world instanceof ServerLevel _levelx) {
                     _levelx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.TOXICFUMESPINK.get(), x, y + 1.0, z, 350, 1.0, 1.0, 1.0, 0.15);
                  }
               }
            }

            if ((entity instanceof ThesmokerEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(ThesmokerEntity.DATA_fume_whezeticks) : 0) > -1
               && entity instanceof ThesmokerEntity _datEntSetI) {
               _datEntSetI.getEntityData()
                  .set(
                     ThesmokerEntity.DATA_fume_whezeticks,
                     (entity instanceof ThesmokerEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(ThesmokerEntity.DATA_fume_whezeticks) : 0) - 1
                  );
            }
         }

         if (entity.isOnFire()) {
            entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 1.0F);
         }

         if ((entity instanceof ThesmokerEntity _datEntI ? (Integer)_datEntI.getEntityData().get(ThesmokerEntity.DATA_axe) : 0) == 1) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 50, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity) {
               ItemStack _setstack = new ItemStack(Items.IRON_AXE).copy();
               _setstack.setCount(1);
               _entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
               if (_entity instanceof Player _player) {
                  _player.getInventory().setChanged();
               }
            }
         }
      }
   }
}
