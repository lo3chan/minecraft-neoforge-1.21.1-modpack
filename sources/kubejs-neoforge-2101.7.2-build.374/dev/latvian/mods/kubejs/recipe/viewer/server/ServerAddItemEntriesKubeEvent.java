package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.AddEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ServerAddItemEntriesKubeEvent implements AddEntriesKubeEvent {
   private final List<ItemStack> list;

   public ServerAddItemEntriesKubeEvent(List<ItemStack> list) {
      this.list = list;
   }

   @Override
   public void add(Context cx, Object[] items) {
      for (Object item : items) {
         this.list.add(ItemWrapper.wrap(cx, item));
      }
   }
}
