package net.blay09.mods.balm.neoforge.container;

import net.blay09.mods.balm.api.container.ExtractionAwareContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class BalmInvWrapper extends InvWrapper {
   public BalmInvWrapper(Container inv) {
      super(inv);
   }

   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return this.getInv() instanceof ExtractionAwareContainer extractionAwareContainer && !extractionAwareContainer.canExtractItem(slot)
         ? ItemStack.EMPTY
         : super.extractItem(slot, amount, simulate);
   }
}
