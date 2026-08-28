/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.recipes;

import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.RecipeLayoutDrawableErrored;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;

public final class RecipeLayoutWithButtonsErrored<R>
implements IRecipeLayoutWithButtons<R> {
    private final RecipeLayoutDrawableErrored<R> errorLayout;

    public RecipeLayoutWithButtonsErrored(IRecipeLayoutDrawable<R> brokenRecipeLayout) {
        ScalableDrawable recipeBackground = Internal.getTextures().getRecipeBackground();
        this.errorLayout = new RecipeLayoutDrawableErrored<R>(brokenRecipeLayout.getRecipeCategory(), brokenRecipeLayout.getRecipe(), recipeBackground, 4);
        Rect2i rect = brokenRecipeLayout.getRect();
        this.errorLayout.setPosition(rect.getX(), rect.getY());
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.errorLayout.drawRecipe(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void updateBounds(int recipeXOffset, int recipeYOffset) {
        Rect2i rectWithBorder = this.errorLayout.getRectWithBorder();
        Rect2i rect = this.errorLayout.getRect();
        this.errorLayout.setPosition(recipeXOffset - rectWithBorder.getX() + rect.getX(), recipeYOffset - rectWithBorder.getY() + rect.getY());
    }

    @Override
    public int totalWidth() {
        Rect2i area = this.errorLayout.getRect();
        Rect2i areaWithBorder = this.errorLayout.getRectWithBorder();
        int leftBorderWidth = area.getX() - areaWithBorder.getX();
        int rightAreaWidth = areaWithBorder.getWidth() - leftBorderWidth;
        return leftBorderWidth + rightAreaWidth;
    }

    @Override
    public IUserInputHandler createUserInputHandler() {
        return new CombinedInputHandler("RecipeLayoutWithButtonsErrored", new UserInputHandler<R>(this.errorLayout));
    }

    @Override
    public void tick() {
        this.errorLayout.tick();
    }

    @Override
    public IRecipeLayoutDrawable<R> getRecipeLayout() {
        return this.errorLayout;
    }

    @Override
    @Nullable
    public RecipeBookmark<?, ?> getRecipeBookmark() {
        return null;
    }

    @Override
    public void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    public int getMissingCountHint() {
        return Integer.MAX_VALUE;
    }

    private record UserInputHandler<R>(IRecipeLayoutDrawable<R> recipeLayout) implements IUserInputHandler
    {
        @Override
        public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
            double mouseY;
            double mouseX = input.getMouseX();
            if (this.recipeLayout.isMouseOver(mouseX, mouseY = input.getMouseY()) && this.recipeLayout.getInputHandler().handleInput(mouseX, mouseY, input)) {
                return Optional.of(this);
            }
            return Optional.empty();
        }

        @Override
        public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
            if (this.recipeLayout.isMouseOver(mouseX, mouseY) && this.recipeLayout.getInputHandler().handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY)) {
                return Optional.of(this);
            }
            return Optional.empty();
        }
    }
}

