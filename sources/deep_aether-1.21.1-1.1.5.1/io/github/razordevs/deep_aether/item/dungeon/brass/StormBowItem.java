package io.github.razordevs.deep_aether.item.dungeon.brass;

import io.github.razordevs.deep_aether.entity.StormArrow;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class StormBowItem extends BowItem {
   public StormBowItem(Properties properties) {
      super(properties);
   }

   public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
      return (AbstractArrow)(arrow.getOwner() instanceof LivingEntity
         ? new StormArrow((LivingEntity)arrow.getOwner(), arrow.level(), projectileStack, weaponStack)
         : super.customArrow(arrow, projectileStack, weaponStack));
   }
}
