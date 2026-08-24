package vazkii.psi.common.item.component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICADAssembly;
import vazkii.psi.common.Psi;

public class ItemCADAssembly extends ItemCADComponent implements ICADAssembly {
   private final String model;

   public ItemCADAssembly(Properties props, String model) {
      super(props);
      this.model = model;
   }

   @Override
   public EnumCADComponent getComponentType(ItemStack stack) {
      return EnumCADComponent.ASSEMBLY;
   }

   @Override
   public ResourceLocation getCADModel(ItemStack stack, ItemStack cad) {
      return Psi.location("item/" + this.model);
   }

   @Override
   public ResourceLocation getCadTexture(ItemStack stack, ItemStack cad) {
      return Psi.location(this.model);
   }
}
