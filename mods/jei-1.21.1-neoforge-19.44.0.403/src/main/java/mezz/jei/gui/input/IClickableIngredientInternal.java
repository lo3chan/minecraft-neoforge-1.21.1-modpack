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
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.world.item.ItemStack;

public interface IClickableIngredientInternal<T> {
    public ITypedIngredient<T> getTypedIngredient();

    public IElement<T> getElement();

    public boolean isMouseOver(double var1, double var3);

    public ItemStack getCheatItemStack(IIngredientManager var1);

    public boolean canClickToFocus();

    default public void show(IRecipesGui recipesGui, FocusUtil focusUtil, List<RecipeIngredientRole> roles) {
        this.getElement().show(recipesGui, focusUtil, roles);
    }
}

