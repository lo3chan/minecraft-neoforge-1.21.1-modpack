package vazkii.psi.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemStressExosuitSensor extends ItemExosuitSensor {
   public ItemStressExosuitSensor(Properties properties) {
      super(properties);
   }

   @Override
   public int getColor(ItemStack stack) {
      return -29499;
   }

   @Override
   public String getEventType(ItemStack stack) {
      return "psi.event.low_hp";
   }
}
