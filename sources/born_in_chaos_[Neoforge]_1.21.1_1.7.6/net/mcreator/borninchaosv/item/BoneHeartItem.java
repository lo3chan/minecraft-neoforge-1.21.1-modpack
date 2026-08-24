package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.procedures.BoneHeartDopolnitielnaiaInformatsiiaProcedure;
import net.mcreator.borninchaosv.procedures.BoneHeartPriShchielchkiePKMProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BoneHeartItem extends Item {
   public BoneHeartItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      if (itemstack.getEntityRepresentation() != null) {
         itemstack.getEntityRepresentation();
      }

      String hoverText = BoneHeartDopolnitielnaiaInformatsiiaProcedure.execute();
      if (hoverText != null) {
         for (String line : hoverText.split("\n")) {
            list.add(Component.literal(line));
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      BoneHeartPriShchielchkiePKMProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, (ItemStack)ar.getObject());
      return ar;
   }
}
