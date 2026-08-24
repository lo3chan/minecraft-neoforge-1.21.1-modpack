package vazkii.psi.api.cad;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.api.PsiAPI;

public interface ICADAssembly {
   default ItemStack createCADStack(ItemStack stack, List<ItemStack> allComponents) {
      return PsiAPI.internalHandler.createDefaultCAD(allComponents);
   }

   @OnlyIn(Dist.CLIENT)
   ResourceLocation getCADModel(ItemStack var1, ItemStack var2);

   @OnlyIn(Dist.CLIENT)
   ResourceLocation getCadTexture(ItemStack var1, ItemStack var2);
}
