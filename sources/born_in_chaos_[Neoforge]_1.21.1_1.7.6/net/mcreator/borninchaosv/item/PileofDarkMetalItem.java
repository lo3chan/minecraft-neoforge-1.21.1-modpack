package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;

public class PileofDarkMetalItem extends Item {
   public PileofDarkMetalItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.EAT;
   }
}
