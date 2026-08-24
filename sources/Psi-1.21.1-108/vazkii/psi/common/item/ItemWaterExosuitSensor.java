package vazkii.psi.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemWaterExosuitSensor extends ItemExosuitSensor {
   public ItemWaterExosuitSensor(Properties properties) {
      super(properties);
   }

   @Override
   public int getColor(ItemStack stack) {
      return -15511297;
   }

   @Override
   public String getEventType(ItemStack stack) {
      return "psi.event.underwater";
   }
}
