package vectorwing.farmersdelight.integration.crafttweaker.handlers;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.MCItemStack;
import com.blamejared.crafttweaker.api.item.MCItemStackMutable;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Input;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Metadata;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents.Output;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler.For;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.blamejared.crafttweaker.api.util.random.Percentaged;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;

@For(CuttingBoardRecipe.class)
public class CuttingBoardRecipeHandler implements IRecipeHandler<CuttingBoardRecipe> {
   public String dumpToCommandString(IRecipeManager<? super CuttingBoardRecipe> manager, RegistryAccess registryAccess, RecipeHolder<CuttingBoardRecipe> recipe) {
      return String.format(
         "%s.addRecipe(%s, %s, %s, %s, %s);",
         manager.getCommandString(),
         StringUtil.quoteAndEscape(recipe.id()),
         IIngredient.fromIngredient((Ingredient)((CuttingBoardRecipe)recipe.value()).getIngredients().get(0)).getCommandString(),
         ((CuttingBoardRecipe)recipe.value())
            .getResults()
            .stream()
            .map(MCItemStackMutable::new)
            .<CharSequence>map(IItemStack::getCommandString)
            .collect(Collectors.joining(", ", "[", "]")),
         IIngredient.fromIngredient(((CuttingBoardRecipe)recipe.value()).getTool()).getCommandString(),
         ((CuttingBoardRecipe)recipe.value()).getSoundEvent().map(BuiltInRegistries.SOUND_EVENT::getKey)
      );
   }

   public <U extends Recipe<?>> boolean doesConflict(IRecipeManager<? super CuttingBoardRecipe> manager, CuttingBoardRecipe firstRecipe, U secondRecipe) {
      return firstRecipe.equals(secondRecipe);
   }

   public Optional<IDecomposedRecipe> decompose(IRecipeManager<? super CuttingBoardRecipe> manager, RegistryAccess registryAccess, CuttingBoardRecipe recipe) {
      IDecomposedRecipe decomposedRecipe = IDecomposedRecipe.builder()
         .with(Input.INGREDIENTS, recipe.getIngredients().stream().map(IIngredient::fromIngredient).toList())
         .with(RecipeHandlerUtils.TOOL_COMPONENT, IIngredient.fromIngredient(recipe.getTool()))
         .with(Metadata.GROUP, recipe.getGroup())
         .with(
            Output.CHANCED_ITEMS,
            recipe.getRollableResults().stream().map(chanceResult -> new MCItemStack(chanceResult.stack()).percent(chanceResult.chance())).toList()
         )
         .build();
      if (recipe.getSoundEvent().isPresent()) {
         decomposedRecipe.set(RecipeHandlerUtils.SOUND_COMPONENT, recipe.getSoundEvent().get());
      }

      return Optional.of(decomposedRecipe);
   }

   public Optional<CuttingBoardRecipe> recompose(IRecipeManager<? super CuttingBoardRecipe> manager, RegistryAccess registryAccess, IDecomposedRecipe recipe) {
      String group = (String)recipe.getOrThrowSingle(Metadata.GROUP);
      List<IIngredient> ingredients = recipe.getOrThrow(Input.INGREDIENTS);
      IIngredient tool = (IIngredient)recipe.getOrThrowSingle(RecipeHandlerUtils.TOOL_COMPONENT);
      IIngredient[] ingredientArray = ingredients.toArray(IIngredient[]::new);
      List<Percentaged<IItemStack>> results = recipe.getOrThrow(Output.CHANCED_ITEMS);
      NonNullList<ChanceResult> stackedResults = NonNullList.create();
      stackedResults.addAll(
         results.stream()
            .map(
               iItemStackPercentaged -> new ChanceResult(
                  ((IItemStack)iItemStackPercentaged.getData()).getInternal(), (float)iItemStackPercentaged.getPercentage()
               )
            )
            .toList()
      );
      List<SoundEvent> soundList = recipe.get(RecipeHandlerUtils.SOUND_COMPONENT);
      Optional<SoundEvent> sound = soundList == null ? Optional.empty() : Optional.of(soundList.get(0));
      Ingredient input = ingredientArray[0].asVanillaIngredient();
      return Optional.of(new CuttingBoardRecipe(group, input, tool.asVanillaIngredient(), stackedResults, sound));
   }
}
