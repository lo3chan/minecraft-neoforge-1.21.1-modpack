package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.WitherballEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NecrothOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.SMOKE, x, y, z, 30, entity.getBbWidth(), entity.getBbHeight() - 0.1, entity.getBbWidth() - 0.1, 0.001);
         }

         if (!(entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(MobEffects.LEVITATION))
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) == entityiterator && entity.isAlive()) {
                  if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 0, false, false));
                  }

                  if (world instanceof Level _level) {
                     if (!_level.isClientSide()) {
                        _level.playSound(
                           null,
                           BlockPos.containing(x, y, z),
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:detahcharge")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F
                        );
                     } else {
                        _level.playLocalSound(
                           x,
                           y,
                           z,
                           (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:detahcharge")),
                           SoundSource.NEUTRAL,
                           1.0F,
                           1.0F,
                           false
                        );
                     }
                  }

                  UndeadRevamp2Mod.queueServerWork(
                     53,
                     () -> {
                        if (entity.isAlive()) {
                           entity.lookAt(Anchor.EYES, new Vec3(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()));
                           if (world instanceof ServerLevel projectileLevel) {
                              Projectile _entityToSpawn = (new Object() {
                                 public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                                    AbstractArrow entityToSpawn = new WitherballEntity((EntityType)UndeadRevamp2ModEntities.WITHERBALL.get(), level) {
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
                              }).getArrow(projectileLevel, entity, 3.0F, 0, (byte)0);
                              _entityToSpawn.setPos(
                                 entity.getX() + entity.getLookAngle().x,
                                 entity.getY() + entity.getLookAngle().y + 1.5 - 0.3,
                                 entity.getZ() + entity.getLookAngle().z
                              );
                              _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 2.0F, 0.0F);
                              projectileLevel.addFreshEntity(_entityToSpawn);
                           }

                           if (world instanceof Level _levelx) {
                              if (!_levelx.isClientSide()) {
                                 _levelx.playSound(
                                    null,
                                    BlockPos.containing(x, y, z),
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.shoot")),
                                    SoundSource.NEUTRAL,
                                    1.0F,
                                    1.0F
                                 );
                              } else {
                                 _levelx.playLocalSound(
                                    x,
                                    y,
                                    z,
                                    (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.wither.shoot")),
                                    SoundSource.NEUTRAL,
                                    1.0F,
                                    1.0F,
                                    false
                                 );
                              }
                           }

                           if (!entity.level().isClientSide()) {
                              entity.discard();
                           }
                        }
                     }
                  );
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt32
            && _livEnt32.hasEffect(MobEffects.LEVITATION)
            && Math.random() < 0.1
            && world instanceof ServerLevel _levelx) {
            _levelx.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 2, 1.0, 1.0, 1.0, 0.001);
         }
      }
   }
}
