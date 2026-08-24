package mezz.jei.api.helpers;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IStackHelper {
   Object getUidForStack(ItemStack var1, UidContext var2);

   Object getUidForStack(ITypedIngredient<ItemStack> var1, UidContext var2);

   boolean isEquivalent(@Nullable ItemStack var1, @Nullable ItemStack var2, UidContext var3);

   @Deprecated(
      since = "19.9.0",
      forRemoval = true
   )
   String getUniqueIdentifierForStack(ItemStack var1, UidContext var2);
}
