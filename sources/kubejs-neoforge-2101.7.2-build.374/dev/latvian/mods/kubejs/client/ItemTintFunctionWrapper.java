package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.item.ItemTintFunction;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public record ItemTintFunctionWrapper(ItemTintFunction function) implements ItemColor {
   public int getColor(ItemStack stack, int index) {
      KubeColor c = this.function.getColor(stack, index);
      return c == null ? -1 : c.kjs$getARGB();
   }
}
