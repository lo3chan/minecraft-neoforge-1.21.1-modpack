package net.mcreator.borninchaosv.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class HolidayCandyItem extends Item {
   public HolidayCandyItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(2).saturationModifier(2.0F).alwaysEdible().build()));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 20;
   }
}
