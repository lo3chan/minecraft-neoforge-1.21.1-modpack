package dev.latvian.mods.kubejs.integration.emi;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class EMIAddInformationKubeEvent implements AddInformationKubeEvent {
   private final RecipeViewerEntryType type;
   private final EmiRegistry registry;

   public EMIAddInformationKubeEvent(RecipeViewerEntryType type, EmiRegistry registry) {
      this.type = type;
      this.registry = registry;
   }

   @Override
   public void add(Context cx, Object filter, List<Component> info) {
      Object in = this.type.wrapPredicate(cx, filter);
      if (this.type == RecipeViewerEntryType.ITEM) {
         this.registry.addRecipe(new EmiInfoRecipe(List.of(EmiIngredient.of((Ingredient)in)), info, null));
      } else if (this.type == RecipeViewerEntryType.FLUID) {
         this.registry.addRecipe(new EmiInfoRecipe(List.of(EMIIntegration.fluidIngredient((FluidIngredient)in)), info, null));
      }
   }
}
