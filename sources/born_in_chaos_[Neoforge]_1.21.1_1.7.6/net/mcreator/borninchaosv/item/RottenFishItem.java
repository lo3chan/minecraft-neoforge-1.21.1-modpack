package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.RottenFishPriZaviershieniiIspolzovaniiaProcedure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class RottenFishItem extends Item {
   public RottenFishItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(3).saturationModifier(0.3F).build()));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 30;
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      RottenFishPriZaviershieniiIspolzovaniiaProcedure.execute(entity);
      return retval;
   }
}
