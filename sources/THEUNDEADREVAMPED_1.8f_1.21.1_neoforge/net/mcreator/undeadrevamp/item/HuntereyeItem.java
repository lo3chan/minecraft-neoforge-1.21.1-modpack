package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.procedures.HuntereyePlayerFinishesUsingItemProcedure;
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

public class HuntereyeItem extends Item {
   public HuntereyeItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON).food(new Builder().nutrition(5).saturationModifier(0.3F).build()));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 10;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.huntereye.description_0"));
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      HuntereyePlayerFinishesUsingItemProcedure.execute(world, x, y, z);
      return retval;
   }
}
