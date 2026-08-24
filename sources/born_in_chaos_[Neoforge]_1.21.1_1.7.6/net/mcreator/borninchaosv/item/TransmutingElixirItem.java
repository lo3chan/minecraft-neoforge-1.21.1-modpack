package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.procedures.TransmutingElixirDopolnitielnaiaInformatsiiaProcedure;
import net.mcreator.borninchaosv.procedures.TransmutingElixirPriPoluchieniiPriedmietaPoRietsieptuProcedure;
import net.mcreator.borninchaosv.procedures.TransmutingElixirPriShchielchkiePKMPoBlokuProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TransmutingElixirItem extends Item {
   public TransmutingElixirItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.EPIC));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      if (itemstack.getEntityRepresentation() != null) {
         itemstack.getEntityRepresentation();
      }

      String hoverText = TransmutingElixirDopolnitielnaiaInformatsiiaProcedure.execute();
      if (hoverText != null) {
         for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
         }
      }
   }

   public InteractionResult useOn(UseOnContext context) {
      super.useOn(context);
      TransmutingElixirPriShchielchkiePKMPoBlokuProcedure.execute(
         context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getItemInHand()
      );
      return InteractionResult.SUCCESS;
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      TransmutingElixirPriPoluchieniiPriedmietaPoRietsieptuProcedure.execute(entity);
   }
}
