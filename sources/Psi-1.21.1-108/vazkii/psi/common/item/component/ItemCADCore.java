package vazkii.psi.common.item.component;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import vazkii.psi.api.cad.EnumCADComponent;

public class ItemCADCore extends ItemCADComponent {
   public ItemCADCore(Properties properties) {
      super(properties);
   }

   @Override
   public EnumCADComponent getComponentType(ItemStack stack) {
      return EnumCADComponent.CORE;
   }
}
