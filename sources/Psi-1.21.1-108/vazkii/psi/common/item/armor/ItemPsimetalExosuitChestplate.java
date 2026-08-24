package vazkii.psi.common.item.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class ItemPsimetalExosuitChestplate extends ItemPsimetalArmor {
   public ItemPsimetalExosuitChestplate(Type type, Properties properties) {
      super(type, properties);
   }

   @Override
   public String getEvent(ItemStack stack) {
      return "psi.event.damage";
   }
}
