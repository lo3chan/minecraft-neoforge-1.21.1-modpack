package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.procedures.SpiritualGingerbreadPriZaviershieniiIspolzovaniiaProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SpiritualGingerbreadItem extends Item {
   public SpiritualGingerbreadItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(4).saturationModifier(1.1F).alwaysEdible().build()));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.born_in_chaos_v1.spiritual_gingerbread.description_0"));
      list.add(Component.translatable("item.born_in_chaos_v1.spiritual_gingerbread.description_1"));
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      SpiritualGingerbreadPriZaviershieniiIspolzovaniiaProcedure.execute(world, x, y, z, entity);
      return retval;
   }
}
