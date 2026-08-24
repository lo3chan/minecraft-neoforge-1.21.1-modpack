package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public class JEIIntegration {
   @Nullable
   public static IIngredientType<?> typeOf(RecipeViewerEntryType type) {
      if (type == RecipeViewerEntryType.ITEM) {
         return VanillaTypes.ITEM_STACK;
      } else {
         return type == RecipeViewerEntryType.FLUID ? NeoForgeTypes.FLUID_STACK : null;
      }
   }

   public static Object[] getEntries(RecipeViewerEntryType type, Context cx, Object filter) {
      if (type == RecipeViewerEntryType.ITEM) {
         return ((ItemPredicate)type.wrapPredicate(cx, filter)).kjs$getStackArray();
      } else {
         return (Object[])(type == RecipeViewerEntryType.FLUID ? ((FluidIngredient)type.wrapPredicate(cx, filter)).getStacks() : new Object[0]);
      }
   }
}
