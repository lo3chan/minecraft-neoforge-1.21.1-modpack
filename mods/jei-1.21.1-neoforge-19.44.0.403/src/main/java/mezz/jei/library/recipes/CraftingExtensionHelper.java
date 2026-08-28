/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
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
import org.jetbrains.annotations.Nullable;

public class CraftingExtensionHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Handler<? extends CraftingRecipe>> handlers = new ArrayList<Handler<? extends CraftingRecipe>>();
    private final Set<Class<? extends CraftingRecipe>> handledClasses = new HashSet<Class<? extends CraftingRecipe>>();
    private final Map<RecipeHolder<? extends CraftingRecipe>, @Nullable ICraftingCategoryExtension<? extends CraftingRecipe>> cache = new IdentityHashMap<RecipeHolder<? extends CraftingRecipe>, ICraftingCategoryExtension<? extends CraftingRecipe>>();

    public <T extends CraftingRecipe> void addRecipeExtension(Class<? extends T> recipeClass, ICraftingCategoryExtension<T> recipeExtension) {
        if (!CraftingRecipe.class.isAssignableFrom(recipeClass)) {
            throw new IllegalArgumentException("Recipe handlers must handle a specific class that inherits from CraftingRecipe. Instead got: " + String.valueOf(recipeClass));
        }
        if (this.handledClasses.contains(recipeClass)) {
            throw new IllegalArgumentException("A Recipe Extension has already been registered for this class:" + String.valueOf(recipeClass));
        }
        this.handledClasses.add(recipeClass);
        this.handlers.add(new Handler<T>(recipeClass, recipeExtension));
    }

    public <R extends CraftingRecipe> ICraftingCategoryExtension<R> getRecipeExtension(IRecipeCategory<RecipeHolder<R>> recipeCategory, RecipeHolder<R> recipeHolder) {
        return this.getOptionalRecipeExtension(recipeHolder).orElseThrow(() -> {
            String recipeName = ErrorUtil.getRecipeInfo(recipeCategory, recipeHolder);
            return new RuntimeException("Failed to create recipe extension for recipe: " + recipeName);
        });
    }

    public <R extends CraftingRecipe> Optional<ICraftingCategoryExtension<R>> getOptionalRecipeExtension(RecipeHolder<R> recipeHolder) {
        if (this.cache.containsKey(recipeHolder)) {
            ICraftingCategoryExtension<? extends CraftingRecipe> extension = this.cache.get(recipeHolder);
            if (extension != null) {
                ICraftingCategoryExtension<? extends CraftingRecipe> cast = extension;
                return Optional.of(cast);
            }
            return Optional.empty();
        }
        Optional<ICraftingCategoryExtension<R>> result = this.getBestRecipeHandler(recipeHolder).map(Handler::getExtension);
        this.cache.put(recipeHolder, result.orElse(null));
        return result;
    }

    private <T extends CraftingRecipe> Stream<Handler<T>> getRecipeHandlerStream(RecipeHolder<T> recipeHolder) {
        return this.handlers.stream().flatMap(handler -> handler.optionalCast(recipeHolder).stream());
    }

    private <T extends CraftingRecipe> Optional<Handler<T>> getBestRecipeHandler(RecipeHolder<T> recipeHolder) {
        Class recipeClass = ((CraftingRecipe)recipeHolder.value()).getClass();
        ArrayList assignableHandlers = new ArrayList();
        List<Handler<T>> allHandlers = this.getRecipeHandlerStream(recipeHolder).toList();
        for (Handler<T> handler : allHandlers) {
            Class handlerRecipeClass = handler.getRecipeClass();
            if (handlerRecipeClass.equals(recipeClass)) {
                return Optional.of(handler);
            }
            assignableHandlers.removeIf(h -> h.getRecipeClass().isAssignableFrom(handlerRecipeClass));
            if (!assignableHandlers.stream().noneMatch(h -> handlerRecipeClass.isAssignableFrom(h.getRecipeClass()))) continue;
            assignableHandlers.add(handler);
        }
        if (assignableHandlers.isEmpty()) {
            return Optional.empty();
        }
        if (assignableHandlers.size() == 1) {
            return Optional.of((Handler)assignableHandlers.getFirst());
        }
        Class superClass = recipeClass;
        while (!Object.class.equals((Object)superClass)) {
            superClass = superClass.getSuperclass();
            for (Handler handler : assignableHandlers) {
                if (!handler.getRecipeClass().equals(superClass)) continue;
                return Optional.of(handler);
            }
        }
        List<Class> assignableClasses = assignableHandlers.stream().map(Handler::getRecipeClass).toList();
        LOGGER.warn("Found multiple matching recipe handlers for {}: {}", recipeClass, assignableClasses);
        return Optional.of((Handler)assignableHandlers.getFirst());
    }

    private record Handler<T extends CraftingRecipe>(Class<? extends T> recipeClass, ICraftingCategoryExtension<T> extension) {
        public <V extends CraftingRecipe> Optional<Handler<V>> optionalCast(RecipeHolder<V> recipeHolder) {
            if (this.isHandled(recipeHolder)) {
                Handler cast = this;
                return Optional.of(cast);
            }
            return Optional.empty();
        }

        public boolean isHandled(RecipeHolder<?> recipeHolder) {
            Recipe recipe = recipeHolder.value();
            if (this.recipeClass.isInstance(recipe)) {
                RecipeHolder<?> cast = recipeHolder;
                return this.extension.isHandled(cast);
            }
            return false;
        }

        public Class<? extends T> getRecipeClass() {
            return this.recipeClass;
        }

        public ICraftingCategoryExtension<T> getExtension() {
            return this.extension;
        }
    }
}

