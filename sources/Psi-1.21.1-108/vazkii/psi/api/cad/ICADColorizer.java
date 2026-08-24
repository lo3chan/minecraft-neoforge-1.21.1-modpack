package vazkii.psi.api.cad;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ICADColorizer extends ICADComponent {
   int DEFAULT_SPELL_COLOR = -15481345;

   @OnlyIn(Dist.CLIENT)
   int getColor(ItemStack var1);

   @Override
   default EnumCADComponent getComponentType(ItemStack stack) {
      return EnumCADComponent.DYE;
   }

   String getContributorName(ItemStack var1);

   void setContributorName(ItemStack var1, String var2);
}
