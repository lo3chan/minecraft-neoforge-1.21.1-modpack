package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentType;

public class JEIRegisterSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
   private final RecipeViewerEntryType type;
   private final IIngredientTypeWithSubtypes ingredientType;
   private final ISubtypeRegistration registration;

   public JEIRegisterSubtypesKubeEvent(RecipeViewerEntryType type, IIngredientTypeWithSubtypes<?, ?> ingredientType, ISubtypeRegistration registration) {
      this.type = type;
      this.ingredientType = ingredientType;
      this.registration = registration;
   }

   @Override
   public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
      JEIRegisterSubtypesKubeEvent.JEISubtypeInterpreter in = new JEIRegisterSubtypesKubeEvent.JEISubtypeInterpreter(interpreter);

      for (Object item : JEIIntegration.getEntries(this.type, cx, filter)) {
         this.registration.registerSubtypeInterpreter(this.ingredientType, this.type.getBase(item), in);
      }
   }

   @Override
   public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
      DataComponentTypeInterpreter in = DataComponentTypeInterpreter.of(components);

      for (Object item : JEIIntegration.getEntries(this.type, cx, filter)) {
         this.registration.registerSubtypeInterpreter(this.ingredientType, this.type.getBase(item), in);
      }
   }

   public record JEISubtypeInterpreter(SubtypeInterpreter interpreter) implements IIngredientSubtypeInterpreter {
      public String apply(Object ingredient, UidContext context) {
         Object o = this.interpreter.apply(ingredient);
         return o == null ? "" : o.toString();
      }
   }
}
