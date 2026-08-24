package mezz.jei.gui.util;

import net.minecraft.world.item.ItemStack;

public enum GiveAmount {
   ONE,
   MAX;

   public int getAmountForStack(ItemStack itemStack) {
      return switch (this) {
         case ONE -> 1;
         case MAX -> itemStack.getMaxStackSize();
      };
   }
}
