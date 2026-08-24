package net.cibernet.alchemancy.properties;

import java.util.Collection;
import javax.annotation.Nullable;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class ExplodingProperty extends Property {
   private final int color;
   final float critRadius;
   final float emergencyRadius;
   private final ExplodingProperty.ExplosionConsumer explosion;

   public ExplodingProperty(int color, float critRadius, float emergencyRadius, ExplodingProperty.ExplosionConsumer explosion) {
      this.color = color;
      this.critRadius = critRadius;
      this.emergencyRadius = emergencyRadius;
      this.explosion = explosion;
   }

   public static ExplodingProperty.ExplosionConsumer destroyBlocks() {
      return (user, stack, target, level, radius) -> {
         ExplosionInteraction interaction = EventHooks.canEntityGrief(level, user) ? ExplosionInteraction.TNT : ExplosionInteraction.NONE;
         level.explode(
            user,
            Explosion.getDefaultDamageSource(level, user),
            null,
            target.getX(),
            target.getEyeY(),
            target.getZ(),
            radius,
            InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BURNING),
            interaction,
            ParticleTypes.EXPLOSION,
            ParticleTypes.EXPLOSION_EMITTER,
            SoundEvents.GENERIC_EXPLODE
         );
         target.hurt(Explosion.getDefaultDamageSource(level, user), radius * 14.0F + 1.0F);
      };
   }

   public static ExplodingProperty.ExplosionConsumer gust() {
      return (user, stack, target, level, radius) -> level.explode(
         null,
         level.damageSources().explosion(user, user),
         AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR,
         target.getX(),
         target.getEyeY(),
         target.getZ(),
         radius,
         false,
         ExplosionInteraction.TRIGGER,
         ParticleTypes.GUST_EMITTER_SMALL,
         ParticleTypes.GUST_EMITTER_LARGE,
         SoundEvents.BREEZE_WIND_CHARGE_BURST
      );
   }

   @Override
   public void onCriticalAttack(Player user, ItemStack weapon, Entity target) {
      this.explode(
         user,
         target,
         EquipmentSlot.MAINHAND,
         PropertyModifierComponent.getOrElse(weapon, this.asHolder(), AlchemancyProperties.Modifiers.ATTACK_RADIUS, this.critRadius),
         weapon
      );
   }

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if (slot.isArmor() && entityLowOnHealth(user)) {
         this.explode(
            user,
            user,
            slot,
            PropertyModifierComponent.getOrElse(weapon, this.asHolder(), AlchemancyProperties.Modifiers.EFFECT_RADIUS, this.emergencyRadius),
            weapon
         );
      }
   }

   private void explode(LivingEntity entity, Entity target, EquipmentSlot slot, float radius, ItemStack stack) {
      Level level = target.level();
      if (!level.isClientSide) {
         this.explosion.apply(entity, stack, target, level, radius);
         spawnLingeringCloud(level, stack, target.position());
         this.damageOrConsumeItem(target.level(), entity, stack, slot, 20);
      }
   }

   private static void spawnLingeringCloud(Level level, ItemStack stack, Vec3 pos) {
      Collection<MobEffectInstance> collection = InfusedPropertiesHelper.getInfusedProperties(stack)
         .stream()
         .filter(p -> p.value() instanceof MobEffectOnHitProperty)
         .map(p -> ((MobEffectOnHitProperty)p.value()).effect)
         .toList();
      if (!collection.isEmpty()) {
         AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, pos.x(), pos.y(), pos.z());
         areaeffectcloud.setRadius(2.5F);
         areaeffectcloud.setRadiusOnUse(-0.5F);
         areaeffectcloud.setWaitTime(10);
         areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
         areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / areaeffectcloud.getDuration());

         for (MobEffectInstance mobeffectinstance : collection) {
            areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
         }

         level.addFreshEntity(areaeffectcloud);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return this.color;
   }

   public interface ExplosionConsumer {
      void apply(@Nullable LivingEntity var1, ItemStack var2, Entity var3, Level var4, float var5);
   }
}
