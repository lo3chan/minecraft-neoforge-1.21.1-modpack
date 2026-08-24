package vazkii.psi.api.cad;

import net.minecraft.world.item.ItemStack;

public interface ICADComponent {
   EnumCADComponent getComponentType(ItemStack var1);

   int getCADStatValue(ItemStack var1, EnumCADStat var2);
}
