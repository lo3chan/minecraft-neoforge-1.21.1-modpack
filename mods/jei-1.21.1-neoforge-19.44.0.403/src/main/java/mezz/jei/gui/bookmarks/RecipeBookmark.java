/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.bookmarks;

import java.util.Objects;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.bookmarks.BookmarkType;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.elements.RecipeBookmarkElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RecipeBookmark<R, I>
implements IBookmark {
    private final IElement<I> element;
    private final IRecipeCategory<R> recipeCategory;
    private final R recipe;
    private final ResourceLocation recipeUid;
    private final ITypedIngredient<I> displayIngredient;
    private final boolean displayIsOutput;
    private boolean visible = true;

    @Nullable
    public static <T> RecipeBookmark<T, ?> create(IRecipeLayoutDrawable<T> recipeLayoutDrawable, IIngredientManager ingredientManager) {
        T recipe = recipeLayoutDrawable.getRecipe();
        IRecipeCategory<T> recipeCategory = recipeLayoutDrawable.getRecipeCategory();
        ResourceLocation recipeUid = recipeCategory.getRegistryName(recipe);
        if (recipeUid == null) {
            return null;
        }
        IRecipeSlotsView recipeSlotsView = recipeLayoutDrawable.getRecipeSlotsView();
        ITypedIngredient<?> output = RecipeBookmark.findFirst(recipeSlotsView, RecipeIngredientRole.OUTPUT);
        if (output != null) {
            output = ingredientManager.normalizeTypedIngredient(output);
            return new RecipeBookmark(recipeCategory, recipe, recipeUid, output, true);
        }
        ITypedIngredient<?> input = RecipeBookmark.findFirst(recipeSlotsView, RecipeIngredientRole.INPUT);
        if (input != null) {
            input = ingredientManager.normalizeTypedIngredient(input);
            return new RecipeBookmark(recipeCategory, recipe, recipeUid, input, false);
        }
        return null;
    }

    @Nullable
    private static ITypedIngredient<?> findFirst(IRecipeSlotsView slotsView, RecipeIngredientRole role) {
        for (IRecipeSlotView slotView : slotsView.getSlotViews()) {
            if (slotView.getRole() != role) continue;
            for (ITypedIngredient<?> ingredient : slotView.getAllIngredientsList()) {
                if (ingredient == null) continue;
                return ingredient;
            }
        }
        return null;
    }

    public RecipeBookmark(IRecipeCategory<R> recipeCategory, R recipe, ResourceLocation recipeUid, ITypedIngredient<I> displayIngredient, boolean displayIsOutput) {
        this.recipeCategory = recipeCategory;
        this.recipe = recipe;
        this.recipeUid = recipeUid;
        this.displayIngredient = displayIngredient;
        this.element = new RecipeBookmarkElement(this);
        this.displayIsOutput = displayIsOutput;
    }

    @Override
    public BookmarkType getType() {
        return BookmarkType.RECIPE;
    }

    public IRecipeCategory<R> getRecipeCategory() {
        return this.recipeCategory;
    }

    public R getRecipe() {
        return this.recipe;
    }

    public ITypedIngredient<I> getDisplayIngredient() {
        return this.displayIngredient;
    }

    public boolean isDisplayIsOutput() {
        return this.displayIsOutput;
    }

    @Override
    public IElement<?> getElement() {
        return this.element;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int hashCode() {
        return Objects.hash(this.recipeUid, this.recipeCategory.getRecipeType());
    }

    public boolean equals(Object obj) {
        if (obj instanceof RecipeBookmark) {
            RecipeBookmark recipeBookmark = (RecipeBookmark)obj;
            return recipeBookmark.recipeUid.equals((Object)this.recipeUid) && this.recipeCategory.getRecipeType().equals(recipeBookmark.recipeCategory.getRecipeType());
        }
        return false;
    }

    public String toString() {
        return "RecipeBookmark{recipeCategory=" + String.valueOf(this.recipeCategory.getRecipeType()) + ", recipe=" + String.valueOf(this.recipe) + ", recipeUid=" + String.valueOf(this.recipeUid) + ", displayIngredient=" + String.valueOf(this.displayIngredient) + ", visible=" + this.visible + "}";
    }

    public <T> boolean isRecipe(RecipeType<T> otherType, T otherRecipe) {
        Class<R> recipeClass;
        RecipeType<R> recipeType = this.recipeCategory.getRecipeType();
        if (recipeType.equals(otherType) && (recipeClass = recipeType.getRecipeClass()).isInstance(otherRecipe)) {
            R castRecipe = recipeClass.cast(otherRecipe);
            ResourceLocation otherUid = this.recipeCategory.getRegistryName(castRecipe);
            return this.recipeUid.equals((Object)otherUid);
        }
        return false;
    }
}

