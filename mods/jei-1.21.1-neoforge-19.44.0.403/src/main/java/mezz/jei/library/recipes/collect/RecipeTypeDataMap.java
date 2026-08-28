/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableListMultimap
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.library.recipes.collect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.recipes.collect.RecipeTypeData;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Unmodifiable;

public class RecipeTypeDataMap {
    private final @Unmodifiable Map<RecipeType<?>, RecipeTypeData<?>> uidMap;

    public RecipeTypeDataMap(List<IRecipeCategory<?>> recipeCategories, ImmutableListMultimap<IRecipeCategory<?>, ITypedIngredient<?>> recipeCategoryCatalystsMap) {
        this.uidMap = recipeCategories.stream().collect(Collectors.toUnmodifiableMap(IRecipeCategory::getRecipeType, recipeCategory -> {
            ImmutableList catalysts = recipeCategoryCatalystsMap.get(recipeCategory);
            return new RecipeTypeData(recipeCategory, (List<ITypedIngredient<?>>)catalysts);
        }));
    }

    public <T> RecipeTypeData<T> get(RecipeType<T> recipeType) {
        RecipeTypeData<?> data = this.uidMap.get(recipeType);
        if (data == null) {
            throw new IllegalStateException("There is no recipe category registered for: " + String.valueOf(recipeType) + "\nA recipe category must be registered in order to use this recipe type.");
        }
        RecipeTypeData<?> recipeTypeData = data;
        return recipeTypeData;
    }

    public void validate(RecipeType<?> recipeType) {
        if (!this.uidMap.containsKey(recipeType)) {
            throw new IllegalStateException("There is no recipe type registered for: " + String.valueOf(recipeType));
        }
    }

    public Optional<RecipeType<?>> getType(ResourceLocation recipeTypeUid) {
        return this.uidMap.keySet().stream().filter(recipeType -> recipeType.getUid().equals((Object)recipeTypeUid)).findFirst();
    }

    public <T> Optional<RecipeType<T>> getType(ResourceLocation recipeTypeUid, Class<? extends T> recipeClass) {
        return this.uidMap.keySet().stream().filter(recipeType -> recipeType.getUid().equals((Object)recipeTypeUid) && recipeType.getRecipeClass().equals(recipeClass)).map(recipeType -> {
            RecipeType castRecipeType = recipeType;
            return castRecipeType;
        }).findFirst();
    }
}

