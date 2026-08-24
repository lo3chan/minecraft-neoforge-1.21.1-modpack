package dev.latvian.mods.kubejs.integration.emi;

import dev.emi.emi.api.EmiRegistry;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class EMIRemoveEntriesKubeEvent implements RemoveEntriesKubeEvent {
   private final RecipeViewerEntryType type;
   private final EmiRegistry registry;

   public EMIRemoveEntriesKubeEvent(RecipeViewerEntryType type, EmiRegistry registry) {
      this.type = type;
      this.registry = registry;
   }

   @Override
   public void remove(Context cx, Object filter) {
      Object predicate = this.type.wrapPredicate(cx, filter);
      if (this.type == RecipeViewerEntryType.ITEM) {
         this.registry.removeEmiStacks(EMIIntegration.predicate((ItemPredicate)predicate));
      } else if (this.type == RecipeViewerEntryType.FLUID) {
         this.registry.removeEmiStacks(EMIIntegration.predicate((FluidIngredient)predicate));
      }
   }
}
