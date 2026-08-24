package vazkii.psi.api.exosuit;

import net.minecraft.world.item.ItemStack;

public interface ISensorHoldable {
   ItemStack getAttachedSensor(ItemStack var1);

   void attachSensor(ItemStack var1, ItemStack var2);
}
