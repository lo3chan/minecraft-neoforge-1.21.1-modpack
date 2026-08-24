package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class ServerRemoveFluidEntriesKubeEvent implements RemoveEntriesKubeEvent {
   private final List<FluidIngredient> removedEntries;

   public ServerRemoveFluidEntriesKubeEvent(List<FluidIngredient> removedEntries) {
      this.removedEntries = removedEntries;
   }

   @Override
   public void remove(Context cx, Object filter) {
      this.removedEntries.add(FluidWrapper.wrapIngredient(cx, filter));
   }
}
