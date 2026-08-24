package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.procedures.DarkMetalIngotPriPoluchieniiPriedmietaPoRietsieptuProcedure;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class DarkMetalIngotItem extends Item {
   public DarkMetalIngotItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.COMMON));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.EAT;
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      DarkMetalIngotPriPoluchieniiPriedmietaPoRietsieptuProcedure.execute(entity);
   }
}
