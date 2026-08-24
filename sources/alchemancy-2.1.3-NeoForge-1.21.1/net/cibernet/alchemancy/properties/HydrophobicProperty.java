package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class HydrophobicProperty extends Property implements IDataHolder<Boolean> {
   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if (user.isInWater() && !this.getData(stack)) {
         activateByEntity(user, user, stack);
      }

      this.setData(stack, user.isInWater());
   }

   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (itemEntity.isInWater() && !this.getData(stack)) {
         activateByEntity(itemEntity, itemEntity, stack);
      }

      this.setData(stack, itemEntity.isInWater());
   }

   @Override
   public void onProjectileTick(ItemStack stack, Projectile projectile) {
      if (projectile.isInWater() && !this.getData(stack)) {
         activateByEntity(projectile, projectile, stack);
      }

      this.setData(stack, projectile.isInWater());
   }

   @Override
   public int getColor(ItemStack stack) {
      return 3316691;
   }

   public Boolean readData(CompoundTag tag) {
      return tag.getBoolean("in_water");
   }

   public CompoundTag writeData(final Boolean data) {
      return new CompoundTag() {
         {
            this.putBoolean("in_water", data);
         }
      };
   }

   public Boolean getDefaultData() {
      return false;
   }
}
