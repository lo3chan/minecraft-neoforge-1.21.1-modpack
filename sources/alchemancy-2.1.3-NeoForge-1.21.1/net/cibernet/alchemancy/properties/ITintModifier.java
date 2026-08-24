package net.cibernet.alchemancy.properties;

import net.minecraft.world.item.ItemStack;

public interface ITintModifier {
   int getTint(ItemStack var1, int var2, int var3, int var4);

   default boolean modifiesAlpha() {
      return false;
   }
}
