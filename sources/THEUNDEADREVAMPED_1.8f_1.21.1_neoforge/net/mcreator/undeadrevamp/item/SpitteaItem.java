package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModItems;
import net.mcreator.undeadrevamp.procedures.SpiiterfriedeggPlayerFinishesUsingItemProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SpitteaItem extends Item {
   public SpitteaItem() {
      super(new Properties().stacksTo(1).rarity(Rarity.COMMON).food(new Builder().nutrition(1).saturationModifier(0.7F).alwaysEdible().build()));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.DRINK;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 8;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.spittea.description_0"));
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = new ItemStack((ItemLike)UndeadRevamp2ModItems.ACIDDICSACKBOWL.get());
      super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      SpiiterfriedeggPlayerFinishesUsingItemProcedure.execute(entity);
      if (itemstack.isEmpty()) {
         return retval;
      } else {
         if (entity instanceof Player player && !player.getAbilities().instabuild && !player.getInventory().add(retval)) {
            player.drop(retval, false);
         }

         return itemstack;
      }
   }
}
