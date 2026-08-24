package net.mcreator.borninchaosv.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MagicalHolidayCandyItem extends Item {
   public MagicalHolidayCandyItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE).food(new Builder().nutrition(2).saturationModifier(5.0F).alwaysEdible().build()));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 20;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFoil(ItemStack itemstack) {
      return true;
   }
}
