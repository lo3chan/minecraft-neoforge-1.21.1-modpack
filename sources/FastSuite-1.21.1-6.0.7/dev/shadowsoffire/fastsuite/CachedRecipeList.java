package dev.shadowsoffire.fastsuite;

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

class CachedRecipeList<C extends RecipeInput, T extends Recipe<C>> {
   static final Map<Class<?>, Boolean> parallelRecipeClassCache = Collections.synchronizedMap(new IdentityHashMap<>());
   static final Map<Class<?>, Boolean> ingredientClassCache = Collections.synchronizedMap(new IdentityHashMap<>());
   private final List<RecipeHolder<T>> serialRecipes;
   private final List<RecipeHolder<T>> parallelRecipes;
   private final RecipeType<T> type;

   public CachedRecipeList(RecipeType<T> type, Collection<RecipeHolder<T>> recipes) {
      this.type = type;
      this.serialRecipes = new ArrayList<>();
      this.parallelRecipes = new ArrayList<>();
      Stopwatch watch = Stopwatch.createStarted();

      for (RecipeHolder<T> holder : recipes) {
         if (this.isParallelRecipe((T)holder.value())) {
            this.parallelRecipes.add(holder);
         } else {
            this.serialRecipes.add(holder);
         }
      }

      watch.stop();
      FastSuite.LOGGER
         .info(
            "Constructed recipe list for {} in {}. {}/{} recipes are parallelized.",
            BuiltInRegistries.RECIPE_TYPE.getKey(type),
            watch,
            this.parallelRecipes.size(),
            recipes.size()
         );
   }

   public Optional<RecipeHolder<T>> getRecipeFor(C inv, Level level) {
      Optional<RecipeHolder<T>> parRecipe = StreamUtils.executeUntil(
         () -> this.parallelRecipes.parallelStream().filter(recipex -> recipex.value().matches(inv, level)).findFirst(),
         FastSuite.maxRecipeLookupTime,
         TimeUnit.SECONDS,
         Optional.empty(),
         () -> timeoutMsg(this.type)
      );
      if (parRecipe.isPresent()) {
         return parRecipe;
      } else {
         for (RecipeHolder<T> recipe : this.serialRecipes) {
            if (recipe.value().matches(inv, level)) {
               return Optional.of(recipe);
            }
         }

         return Optional.empty();
      }
   }

   public List<RecipeHolder<T>> getRecipesFor(C inv, Level level) {
      Comparator<RecipeHolder<T>> recipeSorter = Comparator.comparing(recipe -> recipe.value().getResultItem(level.registryAccess()).getDescriptionId());
      Predicate<RecipeHolder<T>> recipeFilter = recipe -> recipe.value().matches(inv, level);
      List<RecipeHolder<T>> parallelList = StreamUtils.executeUntil(
         () -> this.parallelRecipes.parallelStream().filter(recipeFilter).collect(Collectors.toCollection(ArrayList::new)),
         FastSuite.maxRecipeLookupTime,
         TimeUnit.SECONDS,
         Collections.emptyList(),
         () -> timeoutMsg(this.type)
      );
      parallelList.addAll(this.serialRecipes.stream().filter(recipeFilter).toList());
      Collections.sort(parallelList, recipeSorter);
      return parallelList;
   }

   private boolean isParallelRecipe(T recipe) {
      if (!isSafeRecipeClass(recipe.getClass())) {
         return false;
      } else {
         for (Ingredient ingredient : recipe.getIngredients()) {
            if (!isSafeIngredient(ingredient)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isSafeRecipeClass(Class<?> clz) {
      return parallelRecipeClassCache.computeIfAbsent(clz, c -> c.getName().startsWith("net.minecraft.world.item.crafting."));
   }

   private static boolean isSafeIngredient(Ingredient ingredient) {
      return !ingredient.isCustom()
         ? true
         : ingredientClassCache.computeIfAbsent(
            ingredient.getCustomIngredient().getClass(), clz -> clz.getName().startsWith("net.neoforged.neoforge.common.crafting.")
         );
   }

   static String timeoutMsg(RecipeType<?> type) {
      return String.format(
         "Multithreaded recipe lookup took longer than %d seconds - aborting and returning nothing. Consider blacklisting this recipe type (%s) in the config.",
         FastSuite.maxRecipeLookupTime,
         BuiltInRegistries.RECIPE_TYPE.getKey(type)
      );
   }
}
