/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package mezz.jei.api.recipe.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;

public abstract class AbstractRecipeCategory<T>
implements IRecipeCategory<T> {
    private final RecipeType<T> recipeType;
    private final Component title;
    private final IDrawable icon;
    private final int width;
    private final int height;

    public AbstractRecipeCategory(RecipeType<T> recipeType, Component title, IDrawable icon, int width, int height) {
        this.recipeType = recipeType;
        this.title = title;
        this.icon = icon;
        this.width = width;
        this.height = height;
    }

    @Override
    public final RecipeType<T> getRecipeType() {
        return this.recipeType;
    }

    @Override
    public final Component getTitle() {
        return this.title;
    }

    @Override
    public final IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public final int getWidth() {
        return this.width;
    }

    @Override
    public final int getHeight() {
        return this.height;
    }
}

