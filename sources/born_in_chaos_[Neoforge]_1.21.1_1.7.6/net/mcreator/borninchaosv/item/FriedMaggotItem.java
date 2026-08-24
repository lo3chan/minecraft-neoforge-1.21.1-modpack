package net.mcreator.borninchaosv.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class FriedMaggotItem extends Item {
   public FriedMaggotItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(2).saturationModifier(0.7F).build()));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 22;
   }
}
