package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.mixin.accessors.ItemEntityAccessor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class AncientProperty extends Property {
   @Override
   public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
      if (!itemEntity.level().isClientSide()) {
         ((ItemEntityAccessor)itemEntity).setAge(0);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 6108204;
   }
}
