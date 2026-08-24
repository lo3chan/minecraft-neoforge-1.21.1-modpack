package net.joefoxe.hexerei.events;

import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;

public class WitchArmorEvent {
   @SubscribeEvent
   public void onLivingSetAttackTarget(LivingChangeTargetEvent event) {
      if (event.getEntity() instanceof Witch witch && this.isEquippedBy(event.getNewAboutToBeSetTarget(), 2)) {
         event.setNewAboutToBeSetTarget(null);
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(Pre event) {
      if (event.getEntity() instanceof Witch witch && this.isEquippedBy(witch.getLastHurtByMob(), 2)) {
         witch.setLastHurtByMob(null);
      }
   }

   @SubscribeEvent
   public void onLivingHurt(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
      if (event.getSource().is(DamageTypes.MAGIC)) {
         if (event.getSource().getEntity() instanceof LivingEntity livingEntity && this.isEquippedBy(livingEntity, 3)) {
            event.setNewDamage(event.getOriginalDamage() * 1.25F);
            livingEntity.heal(event.getOriginalDamage() * 0.15F);
         }

         if (this.isEquippedBy(event.getEntity(), 2)) {
            event.setNewDamage(event.getOriginalDamage() / 2.0F);
         }
      }
   }

   private boolean isEquippedBy(LivingEntity entity, int numEquipCheck) {
      int numEquip = 0;
      if (entity == null) {
         return false;
      } else {
         for (ItemStack armorStack : entity.getArmorSlots()) {
            if (armorStack.getItem() instanceof WitchArmorItem) {
               numEquip++;
            }
         }

         return numEquip >= numEquipCheck;
      }
   }
}
