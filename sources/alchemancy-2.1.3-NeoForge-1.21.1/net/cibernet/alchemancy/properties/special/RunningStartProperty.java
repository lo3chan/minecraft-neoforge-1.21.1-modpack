package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.InteractableProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RunningStartProperty extends Property implements IDataHolder<Boolean> {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      super.onEquippedTick(user, slot, stack);
      if (user.isSprinting()) {
         if (user.isSprinting() && !this.getData(stack) && !(user instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem()))) {
            activateByEntity(user, user, stack);
            if (user instanceof Player playerx) {
               InteractableProperty.applyCooldown(playerx, stack, 80);
            }
         }
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      boolean sprinting = user.isSprinting();
      if (sprinting != this.getData(stack)) {
         if (sprinting) {
            this.setData(stack, true);
         } else {
            this.removeData(stack);
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 6029251;
   }

   public Boolean readData(CompoundTag tag) {
      return tag.getBoolean("sprinting");
   }

   public CompoundTag writeData(final Boolean data) {
      return new CompoundTag() {
         {
            this.putBoolean("sprinting", data);
         }
      };
   }

   public Boolean getDefaultData() {
      return false;
   }
}
