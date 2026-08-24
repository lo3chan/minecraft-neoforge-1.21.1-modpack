package vazkii.psi.common.item.component;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemCADColorizerEmpty extends ItemCADColorizer {
   public ItemCADColorizerEmpty(Properties properties) {
      super(properties);
   }

   @Override
   public int getColor(ItemStack stack) {
      return -16250872;
   }
}
