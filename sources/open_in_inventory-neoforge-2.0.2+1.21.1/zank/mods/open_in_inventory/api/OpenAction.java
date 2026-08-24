package zank.mods.open_in_inventory.api;

import net.minecraft.world.item.ItemStack;

public interface OpenAction {
   boolean SNEAK_DEFAULT = false;

   ItemStack stack();

   boolean sneak();

   default boolean match(ItemStack stack) {
      ItemStack match = this.stack();
      return !match.getComponentsPatch().isEmpty() ? ItemStack.matches(match, stack) : stack.is(match.getItem());
   }
}
