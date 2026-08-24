package vazkii.psi.api.cad;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ITileCADAssembler {
   ItemStack getCachedCAD(Player var1);

   void clearCachedCAD();

   ItemStack getStackForComponent(EnumCADComponent var1);

   boolean setStackForComponent(EnumCADComponent var1, ItemStack var2);

   ItemStack getSocketableStack();

   ISocketable getSocketable();

   boolean setSocketableStack(ItemStack var1);

   void onCraftCAD(ItemStack var1);

   boolean isBulletSlotEnabled(int var1);
}
