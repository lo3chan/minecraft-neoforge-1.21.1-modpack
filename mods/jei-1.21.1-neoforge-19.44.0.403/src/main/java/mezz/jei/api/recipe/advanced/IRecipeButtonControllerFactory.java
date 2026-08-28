/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.recipe.advanced;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.buttons.IIconButtonController;
import org.jetbrains.annotations.Nullable;

public interface IRecipeButtonControllerFactory {
    @Nullable
    public <T> IIconButtonController createButtonController(IRecipeLayoutDrawable<T> var1);
}

