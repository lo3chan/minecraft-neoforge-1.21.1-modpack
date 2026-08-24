package vazkii.psi.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import vazkii.psi.client.core.handler.ColorHandler;

public class ItemTriggerExosuitSensor extends ItemExosuitSensor {
   public ItemTriggerExosuitSensor(Properties properties) {
      super(properties);
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorHandler.pulseColor(12346639, 0.1F, 96);
   }

   @Override
   public String getEventType(ItemStack stack) {
      return "psi.event.spell_detonate";
   }
}
