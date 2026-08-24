package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.configuration.MobsabilityConfiguration;
import net.mcreator.undeadrevamp.entity.PregnantneccProjectileEntity;
import net.mcreator.undeadrevamp.entity.TheordureEntity;
import net.mcreator.undeadrevamp.entity.ThepregnantEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModGameRules;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Thepregnant1Procedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) == entityiterator) {
               entity.getPersistentData().putDouble("honeyman_c", 1.0);
            }
         }

         if (entity.getPersistentData().getDouble("honeyman_c") == 1.0
            && entity.getPersistentData().getDouble("honeyman_b") == 0.0
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && entity.isAlive()) {
            entity.getPersistentData().putDouble("honeyman_b", 1.0);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 12, 30, false, false));
            }

            if (entity instanceof ThepregnantEntity) {
               ((ThepregnantEntity)entity).setAnimation("puke");
            }

            UndeadRevamp2Mod.queueServerWork(11, () -> entity.getPersistentData().putDouble("honeyman_a", 1.0));
         }

         if (entity.getPersistentData().getDouble("honeyman_a") == 1.0 && entity.isAlive()) {
            if (entity.getPersistentData().getDouble("pukeshut") == 0.0
               && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
               && !(entity instanceof LivingEntity _livEnt19 && _livEnt19.hasEffect(UndeadRevamp2ModMobEffects.UNDEADSTUNS))) {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:prenantpukes")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:prenantpukes")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel projectileLevel) {
                  Projectile _entityToSpawn = (new Object() {
                        public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                           AbstractArrow entityToSpawn = new PregnantneccProjectileEntity(
                              (EntityType)UndeadRevamp2ModEntities.PREGNANTNECC_PROJECTILE.get(), level
                           ) {
                              public byte getPierceLevel() {
                                 return piercing;
                              }

                              @Override
                              protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
                                 if (knockback > 0) {
                                    double d1 = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                                    Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(knockback * 0.6 * d1);
                                    if (vec3.lengthSqr() > 0.0) {
                                       livingEntity.push(vec3.x, 0.1, vec3.z);
                                    }
                                 }
                              }
                           };
                           entityToSpawn.setOwner(shooter);
                           entityToSpawn.setBaseDamage(damage);
                           entityToSpawn.setSilent(true);
                           return entityToSpawn;
                        }
                     })
                     .getArrow(projectileLevel, entity, 3.0F, 0, (byte)0);
                  _entityToSpawn.setPos(
                     entity.getX() - 0.5 + entity.getLookAngle().x, entity.getY() + entity.getLookAngle().y + 1.5, entity.getZ() + entity.getLookAngle().z
                  );
                  _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 1.5F, 0.0F);
                  projectileLevel.addFreshEntity(_entityToSpawn);
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.BROKENTANK, 12, 0, false, false));
               }
            }

            entity.getPersistentData().putDouble("honeyman_a", 0.0);
            UndeadRevamp2Mod.queueServerWork(150, () -> entity.getPersistentData().putDouble("honeyman_b", 0.0));
         }

         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            Vec3 _centerx = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_centerx, _centerx).inflate(25.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((
                     entityiteratorx.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("forge:allzombiesforge")))
                        || entityiteratorx.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("minecraft:allzombies")))
                  )
                  && !(entityiteratorx instanceof TheordureEntity)
                  && !(entityiteratorx instanceof ThepregnantEntity)
                  && entityiteratorx instanceof Mob _entity
                  && (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity _ent) {
                  _entity.setTarget(_ent);
               }
            }
         }

         if (!(Boolean)MobsabilityConfiguration.ABORTION_NEEDLE.get()
            && entity.getPersistentData().getDouble("honeyman_c") != 1.0
            && entity.getPersistentData().getDouble("babies") > 1.0
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity
            && Math.random() < 0.02) {
            if (Math.random() < (Double)MobsabilityConfiguration.PREG_BIDY.get() / 100.0) {
               if (world instanceof Level _levelx) {
                  if (!_levelx.isClientSide()) {
                     _levelx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beehive.exit")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        -2.0F
                     );
                  } else {
                     _levelx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beehive.exit")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        -2.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.INVISIBLEBIDY.get())
                     .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }

               entity.getPersistentData().putDouble("babies", entity.getPersistentData().getDouble("babies") - 1.0);
            } else {
               if (world instanceof Level _levelxxx) {
                  if (!_levelxxx.isClientSide()) {
                     _levelxxx.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beehive.exit")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        -2.0F
                     );
                  } else {
                     _levelxxx.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.beehive.exit")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        -2.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelxxxx) {
                  Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.SUCKER.get())
                     .spawn(_levelxxxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                  }
               }

               entity.getPersistentData().putDouble("babies", entity.getPersistentData().getDouble("babies") - 1.0);
            }
         }

         if (world.getLevelData().getGameRules().getBoolean(UndeadRevamp2ModGameRules.SUNRAY) && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
            if (world instanceof Level _lvl61
               && _lvl61.isDay()
               && !world.getLevelData().isRaining()
               && !world.getLevelData().isThundering()
               && !entity.isInWaterRainOrBubble()
               && !entity.isOnFire()
               && !world.isClientSide()) {
               entity.igniteForSeconds(5.0F);
            }

            if ((world.getLevelData().isRaining() || world.getLevelData().isThundering()) && !world.isClientSide()) {
               entity.clearFire();
            }
         }

         if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) < 50.0F) {
            if ((entity instanceof ThepregnantEntity _datEntI ? (Integer)_datEntI.getEntityData().get(ThepregnantEntity.DATA_male) : 0) == 0) {
               if (entity instanceof ThepregnantEntity animatable) {
                  animatable.setTexture("woundedthepregnant");
               }
            } else if (entity instanceof ThepregnantEntity animatable) {
               animatable.setTexture("woundedthepregnantex");
            }
         }
      }
   }
}
