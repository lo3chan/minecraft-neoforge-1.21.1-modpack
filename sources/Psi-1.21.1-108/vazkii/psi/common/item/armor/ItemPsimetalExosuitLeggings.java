package vazkii.psi.common.item.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class ItemPsimetalExosuitLeggings extends ItemPsimetalArmor {
   public ItemPsimetalExosuitLeggings(Type type, Properties properties) {
      super(type, properties);
   }

   @Override
   public String getEvent(ItemStack stack) {
      return "psi.event.tick";
   }

   @Override
   public int getCastCooldown(ItemStack stack) {
      return 0;
   }

   @Override
   public float getCastVolume() {
      return 0.0F;
   }
}
