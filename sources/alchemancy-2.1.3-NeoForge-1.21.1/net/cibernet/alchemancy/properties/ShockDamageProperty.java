package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ShockUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ShockDamageProperty extends Property {
   public final float power(ItemStack stack) {
      return PropertyModifierComponent.getOrElse(stack, this.asHolder(), AlchemancyProperties.Modifiers.ATTACK_DAMAGE, 4.0F);
   }

   @Override
   public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
      if (user == null) {
         ShockUtils.environmentalShockAttack(target.level(), target.position(), this.power(weapon));
      } else if (user.equals(damageSource.getEntity())) {
         ShockUtils.meleeShockAttack(user, target, this.power(weapon));
      } else {
         ShockUtils.rangedShockAttack(damageSource.getEntity() == null ? user : damageSource.getEntity(), user, target, this.power(weapon));
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (source == null) {
         ShockUtils.environmentalShockAttack(target.level(), target.position(), this.power(stack));
      } else {
         ShockUtils.selfDamagingMeleeShockAttack(source, target.position(), this.power(stack));
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      if (root.getLevel().getRandom().nextFloat() < 0.01F) {
         ShockUtils.environmentalShockAttack(
            root.getLevel(),
            root.getBlockPos().getCenter(),
            PropertyModifierComponent.getOrElse(root.getItem(), this.asHolder(), AlchemancyProperties.Modifiers.ATTACK_DAMAGE, 3.0F)
         );
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
      playRootedParticles(root, randomSource, ParticleTypes.ELECTRIC_SPARK);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 7595519;
   }
}
