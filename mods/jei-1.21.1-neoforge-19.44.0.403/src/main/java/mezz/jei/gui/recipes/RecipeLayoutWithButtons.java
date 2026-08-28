/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.recipes;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.recipe.advanced.IRecipeButtonControllerFactory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Internal;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipeBookmarkButtonController;
import mezz.jei.gui.recipes.RecipeTransferButtonController;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class RecipeLayoutWithButtons<R>
implements IRecipeLayoutWithButtons<R> {
    private final IRecipeLayoutDrawable<R> recipeLayout;
    private final RecipeTransferButtonController transferButton;
    @Nullable
    private final RecipeBookmark<?, ?> recipeBookmark;
    private final List<IconButton> buttons;

    public static <T> IRecipeLayoutWithButtons<T> create(IRecipeLayoutDrawable<T> recipeLayoutDrawable, @Nullable RecipeBookmark<?, ?> recipeBookmark, BookmarkList bookmarks, RecipesGui recipesGui, List<IRecipeButtonControllerFactory> extraButtonControllerFactories) {
        RecipeTransferButtonController transferButton = new RecipeTransferButtonController(recipeLayoutDrawable, recipesGui);
        RecipeBookmarkButtonController bookmarkButton = new RecipeBookmarkButtonController(bookmarks, recipeBookmark);
        ArrayList<IconButton> buttons = new ArrayList<IconButton>();
        buttons.add(new IconButton(transferButton));
        buttons.add(new IconButton(bookmarkButton));
        for (IRecipeButtonControllerFactory buttonControllerFactory : extraButtonControllerFactories) {
            IIconButtonController buttonController = buttonControllerFactory.createButtonController(recipeLayoutDrawable);
            if (buttonController == null) continue;
            buttons.add(new IconButton(buttonController));
        }
        return new RecipeLayoutWithButtons<T>(recipeLayoutDrawable, transferButton, recipeBookmark, buttons);
    }

    private RecipeLayoutWithButtons(IRecipeLayoutDrawable<R> recipeLayout, RecipeTransferButtonController transferButton, @Nullable RecipeBookmark<?, ?> recipeBookmark, List<IconButton> buttons) {
        this.recipeLayout = recipeLayout;
        this.transferButton = transferButton;
        this.recipeBookmark = recipeBookmark;
        this.buttons = buttons;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.recipeLayout.drawRecipe(guiGraphics, mouseX, mouseY);
        for (IconButton button : this.buttons) {
            if (!button.isVisible()) continue;
            button.draw(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    private ImmutableRect2i getAbsoluteButtonArea(int buttonIndex) {
        Rect2i recipeLayoutRect = this.recipeLayout.getRect();
        Rect2i buttonArea = this.recipeLayout.getSideButtonArea(buttonIndex);
        return new ImmutableRect2i(buttonArea.getX() + recipeLayoutRect.getX(), buttonArea.getY() + recipeLayoutRect.getY(), buttonArea.getWidth(), buttonArea.getHeight());
    }

    @Override
    public void updateBounds(int recipeXOffset, int recipeYOffset) {
        Rect2i rectWithBorder = this.recipeLayout.getRectWithBorder();
        Rect2i rect = this.recipeLayout.getRect();
        this.recipeLayout.setPosition(recipeXOffset - rectWithBorder.getX() + rect.getX(), recipeYOffset - rectWithBorder.getY() + rect.getY());
        int i = 0;
        for (IconButton button : this.buttons) {
            if (!button.isVisible()) continue;
            ImmutableRect2i buttonArea = this.getAbsoluteButtonArea(i);
            if (buttonArea.getWidth() * buttonArea.getHeight() > 0) {
                button.updateBounds(buttonArea);
            }
            ++i;
        }
    }

    @Override
    public int totalWidth() {
        Rect2i area = this.recipeLayout.getRect();
        Rect2i areaWithBorder = this.recipeLayout.getRectWithBorder();
        int leftBorderWidth = area.getX() - areaWithBorder.getX();
        int rightAreaWidth = areaWithBorder.getWidth() - leftBorderWidth;
        int i = 0;
        for (IconButton button : this.buttons) {
            if (!button.isVisible()) continue;
            Rect2i buttonArea = this.recipeLayout.getSideButtonArea(i);
            int buttonRight = buttonArea.getX() + buttonArea.getWidth();
            rightAreaWidth = Math.max(buttonRight, rightAreaWidth);
            ++i;
        }
        return leftBorderWidth + rightAreaWidth;
    }

    @Override
    public IUserInputHandler createUserInputHandler() {
        ArrayList<IUserInputHandler> inputHandlers = new ArrayList<IUserInputHandler>();
        for (IconButton button : this.buttons) {
            inputHandlers.add(button.createInputHandler());
        }
        inputHandlers.add(new RecipeLayoutUserInputHandler<R>(this.recipeLayout));
        return new CombinedInputHandler("RecipeLayoutWithButtons", inputHandlers);
    }

    @Override
    public void tick() {
        this.recipeLayout.tick();
        for (IconButton button : this.buttons) {
            button.tick();
        }
    }

    @Override
    public IRecipeLayoutDrawable<R> getRecipeLayout() {
        return this.recipeLayout;
    }

    @Override
    @Nullable
    public RecipeBookmark<?, ?> getRecipeBookmark() {
        return this.recipeBookmark;
    }

    @Override
    public void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (IconButton button : this.buttons) {
            if (!button.isVisible() || !button.isMouseOver(mouseX, mouseY)) continue;
            button.drawTooltips(guiGraphics, mouseX, mouseY);
            return;
        }
    }

    @Override
    public int getMissingCountHint() {
        return this.transferButton.getMissingCountHint();
    }

    private record RecipeLayoutUserInputHandler<R>(IRecipeLayoutDrawable<R> recipeLayout) implements IUserInputHandler
    {
        @Override
        public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
            double mouseY;
            double mouseX = input.getMouseX();
            if (this.recipeLayout.isMouseOver(mouseX, mouseY = input.getMouseY())) {
                InputConstants.Key key = input.getKey();
                boolean simulate = input.isSimulate();
                if (this.recipeLayout.getInputHandler().handleInput(mouseX, mouseY, input)) {
                    return Optional.of(this);
                }
                IInternalKeyMappings keyMappings = Internal.getKeyMappings();
                if (keyMappings.getCopyRecipeId().isActiveAndMatches(key) && this.handleCopyRecipeId(this.recipeLayout, simulate)) {
                    return Optional.of(this);
                }
            }
            return Optional.empty();
        }

        private boolean handleCopyRecipeId(IRecipeLayoutDrawable<R> recipeLayout, boolean simulate) {
            R recipe;
            if (simulate) {
                return true;
            }
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            IRecipeCategory<R> recipeCategory = recipeLayout.getRecipeCategory();
            ResourceLocation registryName = recipeCategory.getRegistryName(recipe = recipeLayout.getRecipe());
            if (registryName == null) {
                MutableComponent message = Component.translatable((String)"jei.message.copy.recipe.id.failure");
                if (player != null) {
                    player.displayClientMessage((Component)message, false);
                }
                return false;
            }
            String recipeId = registryName.toString();
            minecraft.keyboardHandler.setClipboard(recipeId);
            MutableComponent message = Component.translatable((String)"jei.message.copy.recipe.id.success", (Object[])new Object[]{Component.literal((String)recipeId)});
            if (player != null) {
                player.displayClientMessage((Component)message, false);
            }
            return true;
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

