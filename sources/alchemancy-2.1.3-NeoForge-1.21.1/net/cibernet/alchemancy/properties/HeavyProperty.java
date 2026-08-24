package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class HeavyProperty extends Property {
   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity entity) {
      if (!entity.isNoGravity()) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -0.04, 0.0));
      }
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile entity) {
      if (!entity.isNoGravity()) {
         entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -0.08, 0.0));
         entity.hasImpulse = true;
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (!(user instanceof Player player && player.getAbilities().flying)) {
         Vec3 slowDelta = user.getDeltaMovement().scale(0.5);
         user.setDeltaMovement(new Vec3(slowDelta.x, user.getDeltaMovement().y - 0.025, slowDelta.z));
         user.hasImpulse = true;
      }
   }

   @Override
   public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event) {
      if (slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) {
         entity.playSound(
            (SoundEvent)AlchemancySoundEvents.HEAVY.value(),
            Mth.lerp(entity.fallDistance / 23.0F, 0.1F, 1.0F),
            Mth.lerp(event.getDistance() / entity.getMaxFallDistance(), 1.0F, 0.5F)
         );
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 3490380;
   }
}
