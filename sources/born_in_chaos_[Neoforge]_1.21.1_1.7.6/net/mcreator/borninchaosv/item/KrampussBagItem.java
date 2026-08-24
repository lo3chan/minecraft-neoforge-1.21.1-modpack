package net.mcreator.borninchaosv.item;

import java.util.List;
import net.mcreator.borninchaosv.procedures.KrampussBagPriShchielchkiePKMProcedure;
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

public class KrampussBagItem extends Item {
   public KrampussBagItem() {
      super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.born_in_chaos_v1.krampuss_bag.description_0"));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
      KrampussBagPriShchielchkiePKMProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, (ItemStack)ar.getObject());
      return ar;
   }
}
