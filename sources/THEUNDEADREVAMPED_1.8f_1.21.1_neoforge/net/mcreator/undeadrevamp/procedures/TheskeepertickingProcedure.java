package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.entity.SkeeperthrowprojectileEntity;
import net.mcreator.undeadrevamp.entity.TheskeeperEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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

public class TheskeepertickingProcedure {
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

         if (Math.random() < 0.025 && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.SNEEZE, x, y, z, 5, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.0);
         }

         if (entity.getPersistentData().getDouble("honeyman_c") == 1.0 && entity.getPersistentData().getDouble("honeyman_b") == 0.0) {
            entity.getPersistentData().putDouble("honeyman_b", 1.0);
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 30, false, false));
            }

            if (entity instanceof TheskeeperEntity) {
               ((TheskeeperEntity)entity).setAnimation("throwhoney");
            }

            UndeadRevamp2Mod.queueServerWork(35, () -> {
               entity.getPersistentData().putDouble("honeyman_a", 1.0);
               if (entity instanceof TheskeeperEntity animatable) {
                  animatable.setTexture("skeeprboxless");
               }
            });
         }

         if (entity.getPersistentData().getDouble("honeyman_a") == 1.0 && entity.isAlive()) {
            if (world instanceof ServerLevel projectileLevel) {
               Projectile _entityToSpawn = (new Object() {
                  public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                     AbstractArrow entityToSpawn = new SkeeperthrowprojectileEntity((EntityType)UndeadRevamp2ModEntities.SKEEPERTHROWPROJECTILE.get(), level) {
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
               }).getArrow(projectileLevel, entity, 8.0F, 0, (byte)0);
               _entityToSpawn.setPos(
                  entity.getX() + entity.getLookAngle().x, entity.getY() + entity.getLookAngle().y + 1.5, entity.getZ() + entity.getLookAngle().z
               );
               _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 1.5F, 0.0F);
               projectileLevel.addFreshEntity(_entityToSpawn);
            }

            entity.getPersistentData().putDouble("honeyman_a", 0.0);
         }

         if (entity.getPersistentData().getDouble("honeyman_c") != 1.0 && entity.getPersistentData().getDouble("honeyman_c") != 0.0) {
            entity.getPersistentData().putDouble("honeyman_a", 0.0);
            entity.getPersistentData().putDouble("honeyman_b", 0.0);
            entity.getPersistentData().putDouble("honeyman_c", 0.0);
         }
      }
   }
}
