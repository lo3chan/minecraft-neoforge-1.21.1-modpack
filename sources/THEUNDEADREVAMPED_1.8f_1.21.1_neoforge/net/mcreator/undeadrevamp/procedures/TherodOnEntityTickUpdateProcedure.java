package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.TherodEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModAttributes;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TherodOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator) {
               entity.lookAt(Anchor.EYES, new Vec3(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()));
               if (entity instanceof TherodEntity _datEntSetI) {
                  _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_c, 1);
               }
            }
         }

         if ((entity instanceof TherodEntity _datEntIxxx ? (Integer)_datEntIxxx.getEntityData().get(TherodEntity.DATA_honeyman_c) : 0) == 1
            && (entity instanceof TherodEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(TherodEntity.DATA_honeyman_a) : 0) == 0
            && (entity instanceof TherodEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(TherodEntity.DATA_honeyman_b) : 0) == 0
            && (entity instanceof TherodEntity _datEntI ? (Integer)_datEntI.getEntityData().get(TherodEntity.DATA_tt) : 0) == 0
            && (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity
            && entity.isAlive()) {
            if (entity instanceof TherodEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_a, 0);
            }

            if (entity instanceof TherodEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_b, 1);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:therodcharg")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:therodcharg")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 30, false, false));
            }

            UndeadRevamp2Mod.queueServerWork(30, () -> {
               if (entity instanceof TherodEntity _datEntSetI) {
                  _datEntSetI.getEntityData().set(TherodEntity.DATA_tt, 1);
               }
            });
            if (entity instanceof TherodEntity) {
               ((TherodEntity)entity).setAnimation("sling");
            }
         }

         if ((entity instanceof TherodEntity _datEntIxx ? (Integer)_datEntIxx.getEntityData().get(TherodEntity.DATA_tt) : 0) == 1
            && (entity instanceof TherodEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(TherodEntity.DATA_honeyman_b) : 0) == 1
            && (entity instanceof TherodEntity _datEntI ? (Integer)_datEntI.getEntityData().get(TherodEntity.DATA_honeyman_a) : 0) == 0) {
            if (entity instanceof TherodEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(TherodEntity.DATA_tt, 0);
            }

            if (entity instanceof TherodEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_a, 1);
            }
         }

         if ((entity instanceof TherodEntity _datEntIx ? (Integer)_datEntIx.getEntityData().get(TherodEntity.DATA_honeyman_a) : 0) == 1
            && (entity instanceof TherodEntity _datEntI ? (Integer)_datEntI.getEntityData().get(TherodEntity.DATA_honeyman_b) : 0) == 1) {
            if (entity instanceof TherodEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_a, 0);
            }

            if (entity.isAlive()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity
                  && !(entity instanceof LivingEntity _livEnt33 && _livEnt33.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))
                  && ((TherodEntity)entity).animationprocedure.equals("sling")) {
                  if (world instanceof Level _levelx) {
                     if (!_levelx.isClientSide()) {
                        _levelx.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.player.attack.sweep")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _levelx.playLocalSound(
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

                  entity.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entity.getYRot() + 180.0F)) * 1.25 * 1.2,
                        (Math.sin(Math.toRadians(0.0F - entity.getXRot())) + 0.55) * 1.1,
                        Math.cos(Math.toRadians(entity.getYRot())) * 1.25 * 1.38
                     )
                  );
                  if (entity instanceof TherodEntity _datEntSetI) {
                     _datEntSetI.getEntityData().set(TherodEntity.DATA_activatehitbox, 1);
                  }
               }

               UndeadRevamp2Mod.queueServerWork(
                  (int)(
                     25.0
                        + (
                           entity instanceof LivingEntity _livingEntity41
                                 && _livingEntity41.getAttributes().hasAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED)
                              ? _livingEntity41.getAttribute(UndeadRevamp2ModAttributes.CHEROATTACKSPEED).getValue()
                              : 0.0
                        )
                  ),
                  () -> {
                     if (entity instanceof TherodEntity _datEntSetI) {
                        _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_b, 0);
                     }

                     if (entity instanceof TherodEntity _datEntSetI) {
                        _datEntSetI.getEntityData().set(TherodEntity.DATA_honeyman_c, 0);
                     }
                  }
               );
               UndeadRevamp2Mod.queueServerWork(7, () -> {
                  if (entity instanceof TherodEntity _datEntSetI) {
                     _datEntSetI.getEntityData().set(TherodEntity.DATA_activatehitbox, 0);
                  }
               });
            }
         }

         if ((entity instanceof TherodEntity _datEntI ? (Integer)_datEntI.getEntityData().get(TherodEntity.DATA_activatehitbox) : 0) == 1) {
            Vec3 _centerx = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(entity.getBbHeight() / 2.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiteratorx) {
                  if (entityiteratorx instanceof LivingEntity _livEnt51 && _livEnt51.isBlocking()) {
                     if (world instanceof Level _levelxx) {
                        if (!_levelxx.isClientSide()) {
                           _levelxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _levelxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("item.shield.block")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }

                     entity.setDeltaMovement(
                        new Vec3(
                           Math.sin(Math.toRadians(entityiteratorx.getYRot() + 180.0F)) * 1.25 * 1.1,
                           (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.5) * 1.0,
                           Math.cos(Math.toRadians(entityiteratorx.getYRot())) * 1.25 * 1.3
                        )
                     );
                  } else {
                     if (world instanceof Level _levelxxx) {
                        if (!_levelxxx.isClientSide()) {
                           _levelxxx.playSound(
                              null,
                              BlockPos.containing(x, y, z),
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.attack_iron_door")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F
                           );
                        } else {
                           _levelxxx.playLocalSound(
                              x,
                              y,
                              z,
                              (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.attack_iron_door")),
                              SoundSource.NEUTRAL,
                              1.0F,
                              1.0F,
                              false
                           );
                        }
                     }

                     entityiteratorx.hurt(
                        new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), entity),
                        (float)(
                           entity instanceof LivingEntity _livingEntity58 && _livingEntity58.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                              ? _livingEntity58.getAttribute(Attributes.ATTACK_DAMAGE).getValue()
                              : 0.0
                        )
                     );
                     entityiteratorx.setDeltaMovement(
                        new Vec3(
                           Math.sin(Math.toRadians(entityiteratorx.getYRot() + 180.0F)) * 1.25 * -1.1,
                           (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.5) * 1.12,
                           Math.cos(Math.toRadians(entityiteratorx.getYRot())) * 1.25 * -1.3
                        )
                     );
                  }
               }
            }
         }

         if (world.getLevelData().isThundering() && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _levelxxxx) {
               if (!_levelxxxx.isClientSide()) {
                  _levelxxxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:roddies")),
                     SoundSource.NEUTRAL,
                     8.0F,
                     1.0F
                  );
               } else {
                  _levelxxxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:roddies")),
                     SoundSource.NEUTRAL,
                     8.0F,
                     1.0F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelxxxxx) {
               Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.COPPERTAR.get())
                  .spawn(_levelxxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
               if (entityToSpawn != null) {
               }
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof ServerLevel _levelxxxxxx) {
               LightningBolt entityToSpawn = (LightningBolt)EntityType.LIGHTNING_BOLT.create(_levelxxxxxx);
               entityToSpawn.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
               entityToSpawn.setVisualOnly(true);
               _levelxxxxxx.addFreshEntity(entityToSpawn);
            }

            if (world instanceof ServerLevel _levelxxxxxx) {
               _levelxxxxxx.sendParticles(ParticleTypes.ASH, x, y, z, 500, 3.0, 3.0, 3.0, 0.1);
            }
         }
      }
   }
}
