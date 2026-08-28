/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableListMultimap
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.library.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredientSupplier;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.config.RecipeCategorySortingConfig;
import mezz.jei.library.recipes.InternalRecipeManagerPlugin;
import mezz.jei.library.recipes.PluginManager;
import mezz.jei.library.recipes.RecipeCatalystBuilder;
import mezz.jei.library.recipes.collect.RecipeMap;
import mezz.jei.library.recipes.collect.RecipeTypeData;
import mezz.jei.library.recipes.collect.RecipeTypeDataMap;
import mezz.jei.library.util.IngredientSupplierHelper;
import mezz.jei.library.util.RecipeDebugUtil;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class RecipeManagerInternal {
    private static final Logger LOGGER = LogManager.getLogger();
    private final @Unmodifiable List<IRecipeCategory<?>> recipeCategories;
    private final IIngredientManager ingredientManager;
    private final RecipeTypeDataMap recipeTypeDataMap;
    private final Comparator<IRecipeCategory<?>> recipeCategoryComparator;
    private final EnumMap<RecipeIngredientRole, RecipeMap> recipeMaps;
    private final PluginManager pluginManager;
    private final Set<RecipeType<?>> hiddenRecipeTypes = new HashSet();
    private final IIngredientVisibility ingredientVisibility;
    @Nullable
    private @Unmodifiable List<IRecipeCategory<?>> recipeCategoriesVisibleCache = null;

    public RecipeManagerInternal(List<IRecipeCategory<?>> recipeCategories, ImmutableListMultimap<RecipeType<?>, ITypedIngredient<?>> recipeCatalysts, IIngredientManager ingredientManager, RecipeCategorySortingConfig recipeCategorySortingConfig, IIngredientVisibility ingredientVisibility) {
        ErrorUtil.checkNotEmpty(recipeCategories, "recipeCategories");
        this.ingredientManager = ingredientManager;
        this.ingredientVisibility = ingredientVisibility;
        List<RecipeType> recipeTypes = recipeCategories.stream().map(IRecipeCategory::getRecipeType).toList();
        Comparator<RecipeType<?>> recipeTypeComparator = recipeCategorySortingConfig.getComparator(recipeTypes);
        this.recipeMaps = new EnumMap(RecipeIngredientRole.class);
        for (RecipeIngredientRole role : RecipeIngredientRole.values()) {
            RecipeMap recipeMap = new RecipeMap(recipeTypeComparator, ingredientManager, role);
            this.recipeMaps.put(role, recipeMap);
        }
        this.recipeCategoryComparator = Comparator.comparing(IRecipeCategory::getRecipeType, recipeTypeComparator);
        this.recipeCategories = recipeCategories.stream().sorted(this.recipeCategoryComparator).toList();
        RecipeCatalystBuilder recipeCatalystBuilder = new RecipeCatalystBuilder(this.recipeMaps.get((Object)RecipeIngredientRole.CATALYST));
        for (IRecipeCategory<?> recipeCategory : recipeCategories) {
            RecipeType<?> recipeType = recipeCategory.getRecipeType();
            if (!recipeCatalysts.containsKey(recipeType)) continue;
            ImmutableList catalysts = recipeCatalysts.get(recipeType);
            recipeCatalystBuilder.addCategoryCatalysts(recipeCategory, (List<ITypedIngredient<?>>)catalysts);
        }
        ImmutableListMultimap<IRecipeCategory<?>, ITypedIngredient<?>> recipeCategoryCatalystsMap = recipeCatalystBuilder.buildRecipeCategoryCatalysts();
        this.recipeTypeDataMap = new RecipeTypeDataMap(recipeCategories, recipeCategoryCatalystsMap);
        InternalRecipeManagerPlugin internalRecipeManagerPlugin = new InternalRecipeManagerPlugin(ingredientManager, this.recipeTypeDataMap, this.recipeMaps);
        this.pluginManager = new PluginManager(internalRecipeManagerPlugin);
    }

    public void addPlugins(List<IRecipeManagerPlugin> plugins) {
        this.pluginManager.addAll(plugins);
    }

    public <T> void addRecipes(RecipeType<T> recipeType, List<T> recipes) {
        LOGGER.debug("Adding recipes: {}", recipeType);
        RecipeTypeData recipeTypeData = this.recipeTypeDataMap.get(recipeType);
        IRecipeCategory<T> recipeCategory = recipeTypeData.getRecipeCategory();
        Set<T> hiddenRecipes = recipeTypeData.getHiddenRecipes();
        ArrayList<T> addedRecipes = new ArrayList<T>(recipes.size());
        for (T recipe : recipes) {
            if (!this.addRecipe(recipeCategory, recipe, hiddenRecipes)) continue;
            addedRecipes.add(recipe);
        }
        if (!addedRecipes.isEmpty()) {
            recipeTypeData.addRecipes(addedRecipes);
            this.recipeCategoriesVisibleCache = null;
        }
    }

    private <T> boolean addRecipe(IRecipeCategory<T> recipeCategory, T recipe, Set<T> hiddenRecipes) {
        RecipeType<T> recipeType = recipeCategory.getRecipeType();
        if (hiddenRecipes.contains(recipe)) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipe, recipeCategory, this.ingredientManager);
                LOGGER.debug("Recipe not added because it is hidden: {}", (Object)recipeInfo);
            }
            return false;
        }
        if (!recipeCategory.isHandled(recipe)) {
            if (LOGGER.isDebugEnabled()) {
                String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipe, recipeCategory, this.ingredientManager);
                LOGGER.debug("Recipe not added because the recipe category cannot handle it: {}", (Object)recipeInfo);
            }
            return false;
        }
        IIngredientSupplier ingredientSupplier = IngredientSupplierHelper.getIngredientSupplier(recipe, recipeCategory, this.ingredientManager);
        try {
            for (RecipeMap recipeMap : this.recipeMaps.values()) {
                recipeMap.addRecipe(recipeType, recipe, ingredientSupplier);
            }
            return true;
        }
        catch (LinkageError | RuntimeException e) {
            String recipeInfo = RecipeDebugUtil.getDebugInfoFromRecipe(recipe, recipeCategory, this.ingredientManager);
            LOGGER.error("Found a broken recipe, failed to addRecipe: {}\n", (Object)recipeInfo, (Object)e);
            return false;
        }
    }

    public boolean isCategoryHidden(IRecipeCategory<?> recipeCategory, IFocusGroup focuses) {
        RecipeType<?> recipeType = recipeCategory.getRecipeType();
        if (this.hiddenRecipeTypes.contains(recipeType)) {
            return true;
        }
        if (this.getRecipeCatalystStream(recipeType, true).findAny().isPresent() && this.getRecipeCatalystStream(recipeType, false).findAny().isEmpty()) {
            return true;
        }
        Stream<?> visibleRecipes = this.getRecipesStream(recipeType, focuses, false);
        return visibleRecipes.findAny().isEmpty();
    }

    public Stream<IRecipeCategory<?>> getRecipeCategoriesForTypes(Collection<RecipeType<?>> recipeTypes, IFocusGroup focuses, boolean includeHidden) {
        List<IRecipeCategory<?>> recipeCategories = recipeTypes.stream().map(this.recipeTypeDataMap::get).map(RecipeTypeData::getRecipeCategory).toList();
        return this.getRecipeCategoriesCached(recipeCategories, focuses, includeHidden);
    }

    public <T> IRecipeCategory<T> getRecipeCategory(RecipeType<T> recipeType) {
        RecipeTypeData<T> value = this.recipeTypeDataMap.get(recipeType);
        return value.getRecipeCategory();
    }

    private Stream<IRecipeCategory<?>> getRecipeCategoriesCached(Collection<IRecipeCategory<?>> recipeCategories, IFocusGroup focuses, boolean includeHidden) {
        if (recipeCategories.isEmpty() && focuses.isEmpty() && !includeHidden) {
            if (this.recipeCategoriesVisibleCache == null) {
                this.recipeCategoriesVisibleCache = this.getRecipeCategoriesUncached(recipeCategories, focuses, includeHidden).toList();
            }
            return this.recipeCategoriesVisibleCache.stream();
        }
        return this.getRecipeCategoriesUncached(recipeCategories, focuses, includeHidden);
    }

    private Stream<IRecipeCategory<?>> getRecipeCategoriesUncached(Collection<IRecipeCategory<?>> recipeCategories, IFocusGroup focuses, boolean includeHidden) {
        Stream<Object> categoryStream;
        if (focuses.isEmpty()) {
            categoryStream = recipeCategories.isEmpty() ? this.recipeCategories.stream() : recipeCategories.stream().distinct();
        } else {
            categoryStream = this.pluginManager.getRecipeTypes(focuses).map(this.recipeTypeDataMap::get).map(RecipeTypeData::getRecipeCategory);
            if (!recipeCategories.isEmpty()) {
                categoryStream = categoryStream.filter(recipeCategories::contains);
            }
        }
        if (!includeHidden) {
            categoryStream = categoryStream.filter(c -> !this.isCategoryHidden((IRecipeCategory<?>)c, focuses));
        }
        return categoryStream.sorted(this.recipeCategoryComparator);
    }

    public <T> Stream<T> getRecipesStream(RecipeType<T> recipeType, IFocusGroup focuses, boolean includeHidden) {
        RecipeTypeData<T> recipeTypeData = this.recipeTypeDataMap.get(recipeType);
        return this.pluginManager.getRecipes(recipeTypeData, focuses, includeHidden);
    }

    public <T> Stream<ITypedIngredient<?>> getRecipeCatalystStream(RecipeType<T> recipeType, boolean includeHidden) {
        RecipeTypeData<T> recipeTypeData = this.recipeTypeDataMap.get(recipeType);
        List<ITypedIngredient<?>> catalysts = recipeTypeData.getRecipeCategoryCatalysts();
        if (includeHidden) {
            return catalysts.stream();
        }
        return catalysts.stream().filter(this.ingredientVisibility::isIngredientVisible);
    }

    public <T> void hideRecipes(RecipeType<T> recipeType, Collection<T> recipes) {
        RecipeTypeData<T> recipeTypeData = this.recipeTypeDataMap.get(recipeType);
        Set<T> hiddenRecipes = recipeTypeData.getHiddenRecipes();
        hiddenRecipes.addAll(recipes);
        this.recipeCategoriesVisibleCache = null;
    }

    public <T> void unhideRecipes(RecipeType<T> recipeType, Collection<T> recipes) {
        RecipeTypeData<T> recipeTypeData = this.recipeTypeDataMap.get(recipeType);
        Set<T> hiddenRecipes = recipeTypeData.getHiddenRecipes();
        hiddenRecipes.removeAll(recipes);
        this.recipeCategoriesVisibleCache = null;
    }

    public void hideRecipeCategory(RecipeType<?> recipeType) {
        this.hiddenRecipeTypes.add(recipeType);
        this.recipeCategoriesVisibleCache = null;
    }

    public void unhideRecipeCategory(RecipeType<?> recipeType) {
        this.recipeTypeDataMap.validate(recipeType);
        this.hiddenRecipeTypes.remove(recipeType);
        this.recipeCategoriesVisibleCache = null;
    }

    public <T> Optional<RecipeType<T>> getRecipeType(ResourceLocation recipeUid, Class<? extends T> recipeClass) {
        return this.recipeTypeDataMap.getType(recipeUid, recipeClass);
    }

    public Optional<RecipeType<?>> getRecipeType(ResourceLocation recipeUid) {
        return this.recipeTypeDataMap.getType(recipeUid);
    }

    public void compact() {
        this.recipeMaps.values().forEach(RecipeMap::compact);
    }

    public boolean isRecipeCatalyst(RecipeType<?> recipeType, IFocus<?> focus) {
        RecipeMap recipeMap = this.recipeMaps.get((Object)focus.getRole());
        return recipeMap.isCatalystForRecipeCategory(recipeType, focus.getTypedValue());
    }
}

