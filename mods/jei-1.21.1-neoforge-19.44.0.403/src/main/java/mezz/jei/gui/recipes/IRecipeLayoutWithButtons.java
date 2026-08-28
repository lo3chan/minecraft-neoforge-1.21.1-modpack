/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.recipes;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.input.IUserInputHandler;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

public interface IRecipeLayoutWithButtons<R> {
    public void draw(GuiGraphics var1, int var2, int var3, float var4);

    public void updateBounds(int var1, int var2);

    public int totalWidth();

    public IUserInputHandler createUserInputHandler();

    public void tick();

    public IRecipeLayoutDrawable<R> getRecipeLayout();

    @Nullable
    public RecipeBookmark<?, ?> getRecipeBookmark();

    public void drawTooltips(GuiGraphics var1, int var2, int var3);

    public int getMissingCountHint();
}

