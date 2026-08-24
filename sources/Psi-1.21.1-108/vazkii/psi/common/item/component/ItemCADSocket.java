package vazkii.psi.common.item.component;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import vazkii.psi.api.cad.EnumCADComponent;

public class ItemCADSocket extends ItemCADComponent {
   public static final int MAX_SOCKETS = 12;

   public ItemCADSocket(Properties properties) {
      super(properties);
   }

   @Override
   public EnumCADComponent getComponentType(ItemStack stack) {
      return EnumCADComponent.SOCKET;
   }
}
