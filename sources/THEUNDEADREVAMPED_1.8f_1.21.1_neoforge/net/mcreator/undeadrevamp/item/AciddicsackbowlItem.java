package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.procedures.AciddicsackbowlRightclickedOnBlockProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AciddicsackbowlItem extends Item {
   public AciddicsackbowlItem() {
      super(new Properties().stacksTo(16).rarity(Rarity.COMMON));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.aciddicsackbowl.description_0"));
   }

   public InteractionResult useOn(UseOnContext context) {
      super.useOn(context);
      AciddicsackbowlRightclickedOnBlockProcedure.execute(
         context.getLevel(),
         context.getClickedPos().getX(),
         context.getClickedPos().getY(),
         context.getClickedPos().getZ(),
         context.getLevel().getBlockState(context.getClickedPos()),
         context.getPlayer()
      );
      return InteractionResult.SUCCESS;
   }
}
