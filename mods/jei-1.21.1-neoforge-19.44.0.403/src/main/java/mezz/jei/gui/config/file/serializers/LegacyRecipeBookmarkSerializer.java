/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package mezz.jei.gui.config.file.serializers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.config.IJeiConfigValueSerializer;
import mezz.jei.common.config.file.serializers.DeserializeResult;
import mezz.jei.common.config.file.serializers.LegacyTypedIngredientSerializer;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public class LegacyRecipeBookmarkSerializer {
    private static final String SEPARATOR = "#";
    private final IRecipeManager recipeManager;
    private final IFocusFactory focusFactory;
    private final LegacyTypedIngredientSerializer ingredientSerializer;

    public LegacyRecipeBookmarkSerializer(IRecipeManager recipeManager, IFocusFactory focusFactory, LegacyTypedIngredientSerializer ingredientSerializer) {
        this.recipeManager = recipeManager;
        this.focusFactory = focusFactory;
        this.ingredientSerializer = ingredientSerializer;
    }

    public IJeiConfigValueSerializer.IDeserializeResult<RecipeBookmark<?, ?>> deserialize(String string) {
        ResourceLocation recipeUid;
        ResourceLocation recipeTypeUid;
        String[] parts = string.split(SEPARATOR);
        if (parts.length != 3) {
            String error = "string must be 3 parts";
            return new DeserializeResult<Object>(null, error);
        }
        try {
            recipeTypeUid = ResourceLocation.parse((String)parts[0]);
        }
        catch (RuntimeException e) {
            String error = "recipe type uid must be a valid resource location: %s\n%s".formatted(string, e.getMessage());
            return new DeserializeResult<Object>(null, error);
        }
        try {
            recipeUid = ResourceLocation.parse((String)parts[1]);
        }
        catch (RuntimeException e) {
            String error = "recipe uid must be a valid resource location: %s\n%s".formatted(string, e.getMessage());
            return new DeserializeResult<Object>(null, error);
        }
        IJeiConfigValueSerializer.IDeserializeResult<ITypedIngredient<?>> deserialized = this.ingredientSerializer.deserialize(parts[2]);
        Optional<ITypedIngredient<?>> outputResult = deserialized.getResult();
        if (outputResult.isEmpty()) {
            List<String> errors = deserialized.getErrors();
            return new DeserializeResult<Object>(null, errors);
        }
        Optional<RecipeType<?>> recipeTypeResult = this.recipeManager.getRecipeType(recipeTypeUid);
        if (recipeTypeResult.isEmpty()) {
            String error = "could not find a recipe type matching the given uid: %s".formatted(recipeTypeUid);
            return new DeserializeResult<Object>(null, error);
        }
        ITypedIngredient<?> output = outputResult.get();
        RecipeType<?> recipeType = recipeTypeResult.get();
        IRecipeCategory<?> recipeCategory = this.recipeManager.getRecipeCategory(recipeType);
        return this.createBookmark(string, recipeCategory, recipeUid, output);
    }

    private <T> DeserializeResult<RecipeBookmark<?, ?>> createBookmark(String string, IRecipeCategory<T> recipeCategory, ResourceLocation recipeUid, ITypedIngredient<?> output) {
        IFocus<?> focus = this.focusFactory.createFocus(RecipeIngredientRole.OUTPUT, output);
        Optional<T> recipeResult = this.findRecipe(recipeCategory, List.of(focus), recipeUid);
        if (recipeResult.isEmpty()) {
            String error = "could not find a recipe for this string: %s".formatted(string);
            return new DeserializeResult<Object>(null, error);
        }
        T recipe = recipeResult.get();
        RecipeBookmark recipeBookmark = new RecipeBookmark(recipeCategory, recipe, recipeUid, output, true);
        return new DeserializeResult(recipeBookmark);
    }

    private <T> Optional<T> findRecipe(IRecipeCategory<T> recipeCategory, List<IFocus<?>> focus, ResourceLocation recipeUid) {
        RecipeType<T> recipeType = recipeCategory.getRecipeType();
        return this.recipeManager.createRecipeLookup(recipeType).limitFocus(focus).get().filter(r -> Objects.equals(recipeCategory.getRegistryName(r), recipeUid)).findFirst();
    }
}

