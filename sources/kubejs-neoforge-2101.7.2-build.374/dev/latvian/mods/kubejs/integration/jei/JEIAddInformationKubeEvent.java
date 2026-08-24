package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.network.chat.Component;

public class JEIAddInformationKubeEvent implements AddInformationKubeEvent {
   private final RecipeViewerEntryType type;
   private final IIngredientType ingredientType;
   private final IRecipeRegistration registration;
   private Collection allIngredients;

   public JEIAddInformationKubeEvent(RecipeViewerEntryType type, IIngredientType<?> ingredientType, IRecipeRegistration registration) {
      this.type = type;
      this.ingredientType = ingredientType;
      this.registration = registration;
   }

   @Override
   public void add(Context cx, Object filter, List<Component> info) {
      Predicate in = (Predicate)this.type.wrapPredicate(cx, filter);
      if (this.allIngredients == null) {
         IIngredientManager manager = this.registration.getIngredientManager();
         this.allIngredients = manager.getAllIngredients(this.ingredientType);
      }

      Component[] infoArr = info.toArray(new Component[0]);

      for (Object v : this.allIngredients) {
         if (in.test(v)) {
            this.registration.addIngredientInfo(v, this.ingredientType, infoArr);
         }
      }
   }
}
