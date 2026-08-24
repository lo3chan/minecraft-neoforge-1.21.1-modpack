package net.mcreator.borninchaosv.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DarkUpgradeItem extends Item {
   public DarkUpgradeItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.born_in_chaos_v1.dark_upgrade.description_0"));
      list.add(Component.translatable("item.born_in_chaos_v1.dark_upgrade.description_1"));
      list.add(Component.translatable("item.born_in_chaos_v1.dark_upgrade.description_2"));
      list.add(Component.translatable("item.born_in_chaos_v1.dark_upgrade.description_3"));
      list.add(Component.translatable("item.born_in_chaos_v1.dark_upgrade.description_4"));
      list.add(Component.translatable("item.born_in_chaos_v1.dark_upgrade.description_5"));
   }
}
