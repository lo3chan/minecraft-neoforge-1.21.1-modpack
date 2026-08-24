package net.mcreator.undeadrevamp.procedures;

import net.mcreator.undeadrevamp.entity.QueenbeeperfumeProjectileEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class QueenbeeperfumeRightclickedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
      if (entity != null) {
         if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown(itemstack.getItem(), 150);
         }

         if (world instanceof ServerLevel projectileLevel) {
            Projectile _entityToSpawn = (new Object() {
                  public Projectile getArrow(Level level, Entity shooter, float damage, final int knockback, final byte piercing) {
                     AbstractArrow entityToSpawn = new QueenbeeperfumeProjectileEntity(
                        (EntityType)UndeadRevamp2ModEntities.QUEENBEEPERFUME_PROJECTILE.get(), level
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
               .getArrow(projectileLevel, entity, 1.0F, 0, (byte)0);
            _entityToSpawn.setPos(
               entity.getX() + entity.getLookAngle().x, entity.getY() + entity.getLookAngle().y + 1.5, entity.getZ() + entity.getLookAngle().z
            );
            _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 0.7F, 0.0F);
            projectileLevel.addFreshEntity(_entityToSpawn);
         }

         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:spray")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:spray")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelx) {
            itemstack.hurtAndBreak(1, _levelx, null, _stkprov -> {});
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.swing(InteractionHand.MAIN_HAND, true);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.HONEYSPLAT);
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(UndeadRevamp2ModMobEffects.MOONFLOWERSSCENT);
         }
      }
   }
}
