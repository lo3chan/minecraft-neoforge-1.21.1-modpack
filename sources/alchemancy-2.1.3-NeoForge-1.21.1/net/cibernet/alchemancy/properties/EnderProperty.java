package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.ChorusFruit;

public class EnderProperty extends Property {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      Level level = user.level();
      if (!level.isClientSide()) {
         if (user.isInWaterOrRain() && this.teleport(user, level)) {
            this.damageItem(stack, user, slot);
         } else {
            for (EquipmentSlot slotToCheck : EquipmentSlot.values()) {
               if (InfusedPropertiesHelper.hasProperty(user.getItemBySlot(slotToCheck), AlchemancyProperties.WET) && this.teleport(user, level)) {
                  this.damageItem(stack, user, slot);
               }
            }
         }
      }
   }

   @Override
   public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
      if (!level.isClientSide()) {
         this.teleport(user, level);
      }

      return false;
   }

   @Override
   public void onActivation(Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (!target.level().isClientSide() && target instanceof LivingEntity living) {
         this.teleport(living, target.level());
      }
   }

   private void damageItem(ItemStack stack, LivingEntity entity, EquipmentSlot slot) {
      if (stack.isDamageableItem()) {
         this.damageItem(entity, stack, slot, 10);
      } else if (entity.getRandom().nextBoolean()) {
         this.consumeItem(entity, stack, slot);
      }
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (projectile.isRemoved() && InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHATTERING)) {
         if (rayTraceResult.getType() == Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult) {
            this.onActivation(projectile, entityHitResult.getEntity(), stack);
         }
      } else {
         playParticles(projectile.level(), projectile.position(), projectile.getRandom());
         if (projectile.level() instanceof ServerLevel serverlevel && !projectile.isRemoved()) {
            Entity entity = projectile.getOwner();
            if (entity != null && isAllowedToTeleportOwner(entity, serverlevel)) {
               if (entity.isPassenger()) {
                  entity.unRide();
               }

               if (entity instanceof ServerPlayer serverplayer) {
                  if (serverplayer.connection.isAcceptingMessages() && !event.isCanceled()) {
                     if (projectile.getRandom().nextFloat() < 0.05F && serverlevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                        Endermite endermite = (Endermite)EntityType.ENDERMITE.create(serverlevel);
                        if (endermite != null) {
                           endermite.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                           serverlevel.addFreshEntity(endermite);
                        }
                     }

                     entity.changeDimension(
                        new DimensionTransition(
                           serverlevel, projectile.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING
                        )
                     );
                     entity.resetFallDistance();
                     serverplayer.resetCurrentImpulseContext();
                     entity.hurt(projectile.damageSources().fall(), 5.0F);
                     playSound(serverlevel, projectile.position());
                  }
               } else {
                  entity.changeDimension(
                     new DimensionTransition(
                        serverlevel, projectile.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING
                     )
                  );
                  entity.resetFallDistance();
                  playSound(serverlevel, projectile.position());
               }

               BrittleProperty.breakProjectile(stack, projectile);
            }
         }
      }
   }

   public static void playParticles(Level level, Vec3 pos, RandomSource randomSource) {
      for (int i = 0; i < 32; i++) {
         level.addParticle(
            ParticleTypes.PORTAL, pos.x(), pos.y() + randomSource.nextDouble() * 2.0, pos.z(), randomSource.nextGaussian(), 0.0, randomSource.nextGaussian()
         );
      }
   }

   public static void playSound(Level level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
   }

   private static boolean isAllowedToTeleportOwner(Entity entity, Level level) {
      if (entity.level().dimension() != level.dimension()) {
         return entity.canUsePortal(true);
      } else {
         return entity instanceof LivingEntity livingentity ? livingentity.isAlive() && !livingentity.isSleeping() : entity.isAlive();
      }
   }

   boolean teleport(LivingEntity entityLiving, Level level) {
      for (int i = 0; i < 16; i++) {
         double d0 = entityLiving.getX() + (entityLiving.getRandom().nextDouble() - 0.5) * 16.0;
         double d1 = Mth.clamp(
            entityLiving.getY() + (entityLiving.getRandom().nextInt(16) - 8),
            level.getMinBuildHeight(),
            level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1
         );
         double d2 = entityLiving.getZ() + (entityLiving.getRandom().nextDouble() - 0.5) * 16.0;
         if (entityLiving.isPassenger()) {
            entityLiving.stopRiding();
         }

         Vec3 vec3 = entityLiving.position();
         ChorusFruit event = EventHooks.onChorusFruitTeleport(entityLiving, d0, d1, d2);
         if (event.isCanceled()) {
            return false;
         }

         if (entityLiving.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
            level.gameEvent(GameEvent.TELEPORT, vec3, Context.of(entityLiving));
            SoundSource soundsource = entityLiving.getSoundSource();
            SoundEvent soundevent = entityLiving instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
            level.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), soundevent, soundsource);
            entityLiving.resetFallDistance();
            if (entityLiving instanceof Player player) {
               player.resetCurrentImpulseContext();
            }

            return true;
         }
      }

      return false;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 13369594;
   }
}
