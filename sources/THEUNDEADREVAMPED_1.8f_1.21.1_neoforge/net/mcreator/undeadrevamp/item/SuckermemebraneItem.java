package net.mcreator.undeadrevamp.item;

import net.mcreator.undeadrevamp.procedures.SuckermemebranePlayerFinishesUsingItem_uncookedProcedure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class SuckermemebraneItem extends Item {
   public SuckermemebraneItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(2).saturationModifier(0.1F).build()));
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      SuckermemebranePlayerFinishesUsingItem_uncookedProcedure.execute(entity);
      return retval;
   }
}
