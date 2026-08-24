package mezz.jei.library.recipes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingExtensionHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<CraftingExtensionHelper.Handler<? extends CraftingRecipe>> handlers = new ArrayList<>();
   private final Set<Class<? extends CraftingRecipe>> handledClasses = new HashSet<>();
   private final Map<RecipeHolder<? extends CraftingRecipe>, ICraftingCategoryExtension<? extends CraftingRecipe>> cache = new IdentityHashMap<>();

   public <T extends CraftingRecipe> void addRecipeExtension(Class<? extends T> recipeClass, ICraftingCategoryExtension<T> recipeExtension) {
      if (!CraftingRecipe.class.isAssignableFrom(recipeClass)) {
         throw new IllegalArgumentException("Recipe handlers must handle a specific class that inherits from CraftingRecipe. Instead got: " + recipeClass);
      } else if (this.handledClasses.contains(recipeClass)) {
         throw new IllegalArgumentException("A Recipe Extension has already been registered for this class:" + recipeClass);
      } else {
         this.handledClasses.add(recipeClass);
         this.handlers.add(new CraftingExtensionHelper.Handler<>(recipeClass, recipeExtension));
      }
   }

   public <R extends CraftingRecipe> ICraftingCategoryExtension<R> getRecipeExtension(
      IRecipeCategory<RecipeHolder<R>> recipeCategory, RecipeHolder<R> recipeHolder
   ) {
      return this.<R>getOptionalRecipeExtension(recipeHolder).orElseThrow(() -> {
         String recipeName = ErrorUtil.getRecipeInfo(recipeCategory, recipeHolder);
         return new RuntimeException("Failed to create recipe extension for recipe: " + recipeName);
      });
   }

   public <R extends CraftingRecipe> Optional<ICraftingCategoryExtension<R>> getOptionalRecipeExtension(RecipeHolder<R> recipeHolder) {
      if (this.cache.containsKey(recipeHolder)) {
         ICraftingCategoryExtension<? extends CraftingRecipe> extension = this.cache.get(recipeHolder);
         return extension != null ? Optional.of((ICraftingCategoryExtension<R>)extension) : Optional.empty();
      } else {
         Optional<ICraftingCategoryExtension<R>> result = this.getBestRecipeHandler(recipeHolder).map(CraftingExtensionHelper.Handler::getExtension);
         this.cache.put(recipeHolder, result.orElse(null));
         return result;
      }
   }

   private <T extends CraftingRecipe> Stream<CraftingExtensionHelper.Handler<T>> getRecipeHandlerStream(RecipeHolder<T> recipeHolder) {
      return this.handlers.stream().flatMap(handler -> handler.<T>optionalCast(recipeHolder).stream());
   }

   private <T extends CraftingRecipe> Optional<CraftingExtensionHelper.Handler<T>> getBestRecipeHandler(RecipeHolder<T> recipeHolder) {
      Class<? extends CraftingRecipe> recipeClass = ((CraftingRecipe)recipeHolder.value()).getClass();
      List<CraftingExtensionHelper.Handler<T>> assignableHandlers = new ArrayList<>();

      for (CraftingExtensionHelper.Handler<T> handler : this.getRecipeHandlerStream(recipeHolder).toList()) {
         Class<? extends CraftingRecipe> handlerRecipeClass = handler.getRecipeClass();
         if (handlerRecipeClass.equals(recipeClass)) {
            return Optional.of(handler);
         }

         assignableHandlers.removeIf(h -> h.getRecipeClass().isAssignableFrom(handlerRecipeClass));
         if (assignableHandlers.stream().noneMatch(h -> handlerRecipeClass.isAssignableFrom(h.getRecipeClass()))) {
            assignableHandlers.add(handler);
         }
      }

      if (assignableHandlers.isEmpty()) {
         return Optional.empty();
      } else if (assignableHandlers.size() == 1) {
         return Optional.of((CraftingExtensionHelper.Handler<T>)assignableHandlers.getFirst());
      } else {
         Class<?> superClass = recipeClass;

         while (!Object.class.equals(superClass)) {
            superClass = superClass.getSuperclass();

            for (CraftingExtensionHelper.Handler<T> handler : assignableHandlers) {
               if (handler.getRecipeClass().equals(superClass)) {
                  return Optional.of(handler);
               }
            }
         }

         List<Class<? extends CraftingRecipe>> assignableClasses = assignableHandlers.stream().map(CraftingExtensionHelper.Handler::getRecipeClass).toList();
         LOGGER.warn("Found multiple matching recipe handlers for {}: {}", recipeClass, assignableClasses);
         return Optional.of((CraftingExtensionHelper.Handler<T>)assignableHandlers.getFirst());
      }
   }

   private record Handler<T extends CraftingRecipe>(Class<? extends T> recipeClass, ICraftingCategoryExtension<T> extension) {
      public <V extends CraftingRecipe> Optional<CraftingExtensionHelper.Handler<V>> optionalCast(RecipeHolder<V> recipeHolder) {
         return this.isHandled(recipeHolder) ? Optional.of(this) : Optional.empty();
      }

      public boolean isHandled(RecipeHolder<?> recipeHolder) {
         Recipe<?> recipe = recipeHolder.value();
         return this.recipeClass.isInstance(recipe) ? this.extension.isHandled((RecipeHolder<T>)recipeHolder) : false;
      }

      public Class<? extends T> getRecipeClass() {
         return this.recipeClass;
      }

      public ICraftingCategoryExtension<T> getExtension() {
         return this.extension;
      }
   }
}
