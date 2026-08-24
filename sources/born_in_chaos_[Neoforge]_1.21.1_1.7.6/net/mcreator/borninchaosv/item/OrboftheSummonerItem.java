package net.mcreator.borninchaosv.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;

public class OrboftheSummonerItem extends Item {
   public OrboftheSummonerItem() {
      super(new Properties().stacksTo(16).fireResistant().rarity(Rarity.RARE));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.EAT;
   }
}
