package at.petrak.hexcasting.api.item;

import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.world.item.ItemStack;

public interface VariantItem {
   String TAG_VARIANT = "variant";

   int numVariants();

   default int getVariant(ItemStack stack) {
      return NBTHelper.getInt(stack, "variant", 0);
   }

   default void setVariant(ItemStack stack, int variant) {
      NBTHelper.putInt(stack, "variant", this.clampVariant(variant));
   }

   default int clampVariant(int variant) {
      if (variant < 0) {
         return 0;
      } else {
         return variant >= this.numVariants() ? this.numVariants() - 1 : variant;
      }
   }
}
