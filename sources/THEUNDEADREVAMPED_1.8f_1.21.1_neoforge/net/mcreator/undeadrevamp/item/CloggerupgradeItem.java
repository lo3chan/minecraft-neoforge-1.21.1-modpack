package net.mcreator.undeadrevamp.item;

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

public class CloggerupgradeItem extends Item {
   public CloggerupgradeItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.EPIC));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_0"));
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_1"));
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_2"));
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_3"));
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_4"));
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_5"));
      list.add(Component.translatable("item.undead_revamp2.cloggerupgrade.description_6"));
   }
}
