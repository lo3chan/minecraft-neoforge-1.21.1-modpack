package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.EntitySharkToothArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemModArrow extends ArrowItem {
   public ItemModArrow(Properties group) {
      super(group);
   }

   public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter, ItemStack weapon) {
      return (AbstractArrow)(this == AMItemRegistry.SHARK_TOOTH_ARROW.get()
         ? new EntitySharkToothArrow(worldIn, shooter, stack.copyWithCount(1))
         : super.createArrow(worldIn, stack, shooter, weapon));
   }
}
