package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

public class JaggedProperty extends Property {
   @Override
   public void onItemPickedUp(Player target, ItemStack stack, ItemEntity itemEntity) {
      if (itemEntity.tickCount % 20 == 0 || !itemEntity.hasPickUpDelay()) {
         DamageSource thornsDamage = target.damageSources().thorns(itemEntity);
         if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.NONLETHAL)) {
            target.hurt(thornsDamage, InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHARP) ? 2.0F : 1.0F);
         }

         InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> ((Property)propertyHolder.value()).onAttack(itemEntity, stack, thornsDamage, target));
      }
   }

   @Override
   public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
      if ((slot.isArmor() || user.getUseItem() == weapon)
         && damageSource.getDirectEntity() != null
         && damageSource.getDirectEntity() instanceof LivingEntity livingTarget) {
         DamageContainer thornsDamage = new DamageContainer(user.damageSources().thorns(user), (float)getItemAttackDamage(weapon));
         LivingIncomingDamageEvent incomingDamageEvent = new LivingIncomingDamageEvent(livingTarget, thornsDamage);
         InfusedPropertiesHelper.forEachProperty(
            weapon, propertyHolder -> ((Property)propertyHolder.value()).onIncomingAttack(user, weapon, livingTarget, incomingDamageEvent)
         );
         if (!incomingDamageEvent.isCanceled()) {
            Pre damageEvent = new Pre(livingTarget, thornsDamage);
            InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> ((Property)propertyHolder.value()).modifyAttackDamage(user, weapon, damageEvent));
            livingTarget.hurt(damageEvent.getSource(), damageEvent.getNewDamage());
         }
      }
   }

   @Override
   public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
      if (root.getTickCount() % 10 == 0 && !InfusedPropertiesHelper.hasProperty(root.getItem(), AlchemancyProperties.NONLETHAL)) {
         for (LivingEntity entity : entitiesInBounds) {
            entity.hurt(entity.damageSources().cactus(), InfusedPropertiesHelper.hasProperty(root.getItem(), AlchemancyProperties.SHARP) ? 2.0F : 1.0F);
         }
      }
   }

   @Override
   public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random) {
      playRootedParticles(root, random, ParticleTypes.CRIT);
   }

   @Override
   public int getColor(ItemStack stack) {
      return 6592562;
   }
}
