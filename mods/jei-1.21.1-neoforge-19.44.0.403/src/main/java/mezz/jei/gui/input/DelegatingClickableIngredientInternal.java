/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.gui.input;

import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.world.item.ItemStack;

public class DelegatingClickableIngredientInternal<T>
implements IClickableIngredientInternal<T> {
    private final IClickableIngredientInternal<T> delegate;

    public DelegatingClickableIngredientInternal(IClickableIngredientInternal<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ITypedIngredient<T> getTypedIngredient() {
        return this.delegate.getTypedIngredient();
    }

    @Override
    public IElement<T> getElement() {
        return this.delegate.getElement();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.delegate.isMouseOver(mouseX, mouseY);
    }

    @Override
    public ItemStack getCheatItemStack(IIngredientManager ingredientManager) {
        return this.delegate.getCheatItemStack(ingredientManager);
    }

    @Override
    public boolean canClickToFocus() {
        return this.delegate.canClickToFocus();
    }

    @Override
    public void show(IRecipesGui recipesGui, FocusUtil focusUtil, List<RecipeIngredientRole> roles) {
        this.delegate.show(recipesGui, focusUtil, roles);
    }
}

