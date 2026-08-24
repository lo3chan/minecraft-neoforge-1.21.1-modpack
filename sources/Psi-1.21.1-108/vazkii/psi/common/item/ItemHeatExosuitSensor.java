package vazkii.psi.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemHeatExosuitSensor extends ItemExosuitSensor {
   public ItemHeatExosuitSensor(Properties properties) {
      super(properties);
   }

   @Override
   public int getColor(ItemStack stack) {
      return -57837;
   }

   @Override
   public String getEventType(ItemStack stack) {
      return "psi.event.on_fire";
   }
}
