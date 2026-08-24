package vazkii.psi.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemLightExosuitSensor extends ItemExosuitSensor {
   public ItemLightExosuitSensor(Properties properties) {
      super(properties);
   }

   @Override
   public String getEventType(ItemStack stack) {
      return "psi.event.low_light";
   }

   @Override
   public int getColor(ItemStack stack) {
      return -5101;
   }
}
