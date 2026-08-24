package mezz.jei.common.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import mezz.jei.api.recipe.category.extensions.vanilla.brewing.IBrewingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.brewing.IExtendableBrewingRecipeCategory;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.common.util.ErrorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class BrewingExtensionHelper implements IExtendableBrewingRecipeCategory {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<Class<?>, IBrewingCategoryExtension<?>> extensions = new LinkedHashMap<>();
   private final Map<Class<?>, IBrewingCategoryExtension<?>> resolvedExtensions = new HashMap<>();
   private final Set<Class<?>> unresolvedRecipeClasses = new HashSet<>();

   @Override
   public <R> void addExtension(Class<? extends R> recipeClass, IBrewingCategoryExtension<R> extension) {
      ErrorUtil.checkNotNull(recipeClass, "recipeClass");
      ErrorUtil.checkNotNull(extension, "extension");
      if (this.extensions.containsKey(recipeClass)) {
         throw new IllegalArgumentException("An extension has already been registered for: " + recipeClass);
      } else {
         this.extensions.put(recipeClass, extension);
         this.resolvedExtensions.clear();
         this.unresolvedRecipeClasses.clear();
      }
   }

   @Nullable
   public <R> IBrewingCategoryExtension<? super R> getRecipeExtension(R recipe) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      Class<?> recipeClass = recipe.getClass();
      IBrewingCategoryExtension<?> exactExtension = this.extensions.get(recipeClass);
      if (exactExtension != null) {
         return castExtension(exactExtension);
      } else {
         IBrewingCategoryExtension<?> resolvedExtension = this.resolvedExtensions.get(recipeClass);
         if (resolvedExtension != null) {
            return castExtension(resolvedExtension);
         } else if (this.unresolvedRecipeClasses.contains(recipeClass)) {
            return null;
         } else {
            Entry<Class<?>, IBrewingCategoryExtension<?>> mostSpecificExtension = this.getMostSpecificExtension(recipeClass);
            if (mostSpecificExtension == null) {
               this.unresolvedRecipeClasses.add(recipeClass);
               return null;
            } else {
               resolvedExtension = mostSpecificExtension.getValue();
               this.resolvedExtensions.put(recipeClass, resolvedExtension);
               return castExtension(resolvedExtension);
            }
         }
      }
   }

   public <R> List<IJeiBrewingRecipe> getBrewingRecipes(Collection<R> brewingRecipes, IVanillaRecipeFactory vanillaRecipeFactory) {
      Set<IJeiBrewingRecipe> recipes = new HashSet<>();
      Set<Class<?>> unhandledRecipeClasses = new HashSet<>();

      for (R brewingRecipe : brewingRecipes) {
         IBrewingCategoryExtension<? super R> extension = this.getRecipeExtension(brewingRecipe);
         if (extension == null) {
            Class<?> recipeClass = brewingRecipe.getClass();
            if (unhandledRecipeClasses.add(recipeClass)) {
               LOGGER.debug("Can't handle brewing recipe class: {}", recipeClass);
            }
         } else {
            try {
               for (IJeiBrewingRecipe extensionRecipe : Objects.requireNonNull(
                  extension.getBrewingRecipes(brewingRecipe, vanillaRecipeFactory), "brewing extension recipes"
               )) {
                  recipes.add(Objects.requireNonNull(extensionRecipe, "brewing extension recipe"));
               }
            } catch (LinkageError | RuntimeException var11) {
               LOGGER.error("Failed to handle custom brewing recipe class {} with extension {}", brewingRecipe.getClass(), extension.getClass(), var11);
            }
         }
      }

      return new ArrayList<>(recipes);
   }

   @Nullable
   private Entry<Class<?>, IBrewingCategoryExtension<?>> getMostSpecificExtension(Class<?> recipeClass) {
      Entry<Class<?>, IBrewingCategoryExtension<?>> result = null;

      for (Entry<Class<?>, IBrewingCategoryExtension<?>> candidate : this.extensions.entrySet()) {
         Class<?> candidateClass = candidate.getKey();
         if (candidateClass.isAssignableFrom(recipeClass) && !this.hasMoreSpecificExtension(recipeClass, candidateClass)) {
            if (result != null) {
               this.logAmbiguousExtensions(recipeClass);
               return null;
            }

            result = candidate;
         }
      }

      return result;
   }

   private boolean hasMoreSpecificExtension(Class<?> recipeClass, Class<?> candidateClass) {
      for (Class<?> otherClass : this.extensions.keySet()) {
         if (otherClass != candidateClass && otherClass.isAssignableFrom(recipeClass) && candidateClass.isAssignableFrom(otherClass)) {
            return true;
         }
      }

      return false;
   }

   private void logAmbiguousExtensions(Class<?> recipeClass) {
      List<String> matchingClasses = new ArrayList<>();

      for (Class<?> extensionClass : this.extensions.keySet()) {
         if (extensionClass.isAssignableFrom(recipeClass) && !this.hasMoreSpecificExtension(recipeClass, extensionClass)) {
            matchingClasses.add(extensionClass.getName());
         }
      }

      matchingClasses.sort(String::compareTo);
      LOGGER.warn("Found multiple matching brewing recipe extensions for {}: {}", recipeClass, matchingClasses);
   }

   private static <R> IBrewingCategoryExtension<? super R> castExtension(IBrewingCategoryExtension<?> extension) {
      return (IBrewingCategoryExtension<? super R>)extension;
   }
}
