package net.mehvahdjukaar.moonlight.api.events;

import net.mehvahdjukaar.moonlight.api.events.platform.IDropItemOnDeathEventImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IDropItemOnDeathEvent extends SimpleEvent {
   boolean isBeforeDrop();

   Player getPlayer();

   ItemStack getItemStack();

   void setCanceled(boolean var1);

   boolean isCanceled();

   void setReturnItemStack(ItemStack var1);

   ItemStack getReturnItemStack();

   static IDropItemOnDeathEvent create(ItemStack var0, Player var1, boolean var2) {
      return IDropItemOnDeathEventImpl.create(var0, var1, var2);
   }
}
