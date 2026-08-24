package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.SmokedFleshPriZaviershieniiIspolzovaniiaProcedure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class SmokedFishItem extends Item {
   public SmokedFishItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(3).saturationModifier(1.0F).build()));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 30;
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      SmokedFleshPriZaviershieniiIspolzovaniiaProcedure.execute(entity);
      return retval;
   }
}
