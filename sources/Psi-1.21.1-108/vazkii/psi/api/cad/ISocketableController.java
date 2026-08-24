package vazkii.psi.api.cad;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ISocketableController {
   ItemStack[] getControlledStacks(Player var1, ItemStack var2);

   int getDefaultControlSlot(ItemStack var1);

   void setSelectedSlot(Player var1, ItemStack var2, int var3, int var4);
}
