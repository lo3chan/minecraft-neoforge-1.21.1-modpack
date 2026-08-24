package vectorwing.farmersdelight.integration.crafttweaker.handlers;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.MCItemStackMutable;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Input;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Metadata;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Output;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Processing;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler.For;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.blamejared.crafttweaker.impl.helper.AccessibleElementsProvider;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@For(CookingPotRecipe.class)
public final class CookingPotRecipeHandler implements IRecipeHandler<CookingPotRecipe> {
   public String dumpToCommandString(IRecipeManager<? super CookingPotRecipe> manager, RegistryAccess registryAccess, RecipeHolder<CookingPotRecipe> recipe) {
      return String.format(
         "%s.addRecipe(%s, %s, %s, %s, %s, %s);",
         manager.getCommandString(),
         StringUtil.quoteAndEscape(recipe.id()),
         IItemStack.of(((CookingPotRecipe)recipe.value()).getResultItem(registryAccess)).getCommandString(),
         ((CookingPotRecipe)recipe.value())
            .getIngredients()
            .stream()
            .map(IIngredient::fromIngredient)
            .<CharSequence>map(IIngredient::getCommandString)
            .collect(Collectors.joining(", ", "[", "]")),
         new MCItemStackMutable(((CookingPotRecipe)recipe.value()).getOutputContainer()).getCommandString(),
         ((CookingPotRecipe)recipe.value()).getExperience(),
         ((CookingPotRecipe)recipe.value()).getCookTime()
      );
   }

   public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super CookingPotRecipe> manager, CookingPotRecipe firstRecipe, U secondRecipe) {
      return firstRecipe.equals(secondRecipe);
   }

   public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super CookingPotRecipe> manager, RegistryAccess registryAccess, CookingPotRecipe recipe) {
      IDecomposedRecipe decomposedRecipe = IDecomposedRecipe.builder()
         .with(Output.ITEMS, IItemStack.of((ItemStack)AccessibleElementsProvider.get().registryAccess(recipe::getResultItem)))
         .with(Input.INGREDIENTS, recipe.getIngredients().stream().map(IIngredient::fromIngredient).toList())
         .with(Processing.TIME, recipe.getCookTime())
         .with(Metadata.GROUP, recipe.getGroup())
         .with(RecipeHandlerUtils.CONTAINER_COMPONENT, new MCItemStackMutable(recipe.getOutputContainer()))
         .with(Output.EXPERIENCE, recipe.getExperience())
         .build();
      if (recipe.getRecipeBookTab() != null) {
         decomposedRecipe.set(RecipeHandlerUtils.COOKING_TAB_COMPONENT, recipe.getRecipeBookTab().name());
      }

      return Optional.of(decomposedRecipe);
   }

   public Optional<CookingPotRecipe> recompose(IRecipeManager<? super CookingPotRecipe> manager, RegistryAccess registryAccess, IDecomposedRecipe recipe) {
      IItemStack output = (IItemStack)recipe.getOrThrowSingle(Output.ITEMS);
      List<IIngredient> ingredients = recipe.getOrThrow(Input.INGREDIENTS);
      NonNullList<Ingredient> inputList = NonNullList.create();

      for (IIngredient ingredient : ingredients) {
         if (!ingredient.isEmpty()) {
            inputList.add(ingredient.asVanillaIngredient());
         }
      }

      int time = (Integer)recipe.getOrThrowSingle(Processing.TIME);
      String group = (String)recipe.getOrThrowSingle(Metadata.GROUP);
      IItemStack container = (IItemStack)recipe.getOrThrowSingle(RecipeHandlerUtils.CONTAINER_COMPONENT);
      float exp = (Float)recipe.getOrThrowSingle(Output.EXPERIENCE);
      List<String> cookingRecipeBookTabList = recipe.get(RecipeHandlerUtils.COOKING_TAB_COMPONENT);
      CookingPotRecipeBookTab cookTab = cookingRecipeBookTabList == null ? null : CookingPotRecipeBookTab.valueOf(cookingRecipeBookTabList.get(0));
      return Optional.of(new CookingPotRecipe(group, cookTab, inputList, output.getInternal(), container.getInternal(), exp, time));
   }
}
