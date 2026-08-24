package mezz.jei.api.helpers;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;

public interface IPlatformFluidHelper<T> {
   IIngredientTypeWithSubtypes<Fluid, T> getFluidIngredientType();

   T create(Holder<Fluid> var1, long var2, DataComponentPatch var4);

   T create(Holder<Fluid> var1, long var2);

   long bucketVolume();
}
