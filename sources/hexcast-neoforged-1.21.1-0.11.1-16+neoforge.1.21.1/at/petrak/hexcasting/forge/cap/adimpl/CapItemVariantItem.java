package at.petrak.hexcasting.forge.cap.adimpl;

import at.petrak.hexcasting.api.addldata.ADVariantItem;
import at.petrak.hexcasting.api.item.VariantItem;
import net.minecraft.world.item.ItemStack;

public record CapItemVariantItem(VariantItem variantItem, ItemStack stack) implements ADVariantItem {
   @Override
   public int numVariants() {
      return this.variantItem.numVariants();
   }

   @Override
   public int getVariant() {
      return this.variantItem.getVariant(this.stack);
   }

   @Override
   public void setVariant(int variant) {
      this.variantItem.setVariant(this.stack, variant);
   }
}
