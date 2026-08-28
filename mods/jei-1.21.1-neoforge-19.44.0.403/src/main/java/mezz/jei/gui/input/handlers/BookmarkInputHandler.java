/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.SameElementInputHandler;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.gui.screens.Screen;

public class BookmarkInputHandler
implements IUserInputHandler {
    private final CombinedRecipeFocusSource focusSource;
    private final BookmarkList bookmarkList;
    private final BookmarkOverlay bookmarkOverlay;
    private final IClientConfig clientConfig;
    private final RecipesGui recipesGui;

    public BookmarkInputHandler(CombinedRecipeFocusSource focusSource, BookmarkList bookmarkList, BookmarkOverlay bookmarkOverlay, IClientConfig clientConfig, RecipesGui recipesGui) {
        this.focusSource = focusSource;
        this.bookmarkList = bookmarkList;
        this.bookmarkOverlay = bookmarkOverlay;
        this.clientConfig = clientConfig;
        this.recipesGui = recipesGui;
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (input.is(keyBindings.getBookmark())) {
            Optional<IUserInputHandler> recipeHandler = this.handleRecipeBookmark(input);
            if (recipeHandler.isPresent()) {
                return recipeHandler;
            }
            return this.handleIngredientBookmark(input, keyBindings);
        }
        return Optional.empty();
    }

    private Optional<IUserInputHandler> handleRecipeBookmark(UserInput input) {
        double mouseY;
        double mouseX = input.getMouseX();
        Optional<IRecipeLayoutWithButtons<?>> layoutWithButtons = this.recipesGui.getRecipeLayoutUnderMouse(mouseX, mouseY = input.getMouseY());
        if (layoutWithButtons.isEmpty()) {
            return Optional.empty();
        }
        IRecipeLayoutWithButtons<?> recipeLayoutWithButtons = layoutWithButtons.get();
        RecipeBookmark<?, ?> recipeBookmark = recipeLayoutWithButtons.getRecipeBookmark();
        if (recipeBookmark == null) {
            return Optional.empty();
        }
        IRecipeLayoutDrawable<?> layout = recipeLayoutWithButtons.getRecipeLayout();
        Optional<RecipeSlotUnderMouse> slotUnderMouse = layout.getSlotUnderMouse(mouseX, mouseY);
        if (!BookmarkInputHandler.shouldBookmarkRecipe(slotUnderMouse, (boolean)this.clientConfig.bookmarkOutputAsRecipe().getValue())) {
            return Optional.empty();
        }
        if (!input.isSimulate()) {
            this.bookmarkList.toggleBookmark(recipeBookmark);
        }
        return Optional.of(new SameElementInputHandler(this, layout::isMouseOver));
    }

    static boolean shouldBookmarkRecipe(Optional<RecipeSlotUnderMouse> slotUnderMouse, boolean bookmarkOutputAsRecipeEnabled) {
        return slotUnderMouse.map(slot -> BookmarkInputHandler.shouldBookmarkRecipe(slot.slot().getRole(), bookmarkOutputAsRecipeEnabled)).orElse(true);
    }

    static boolean shouldBookmarkRecipe(RecipeIngredientRole role, boolean bookmarkOutputAsRecipeEnabled) {
        return role == RecipeIngredientRole.OUTPUT && bookmarkOutputAsRecipeEnabled;
    }

    private Optional<IUserInputHandler> handleIngredientBookmark(UserInput input, IInternalKeyMappings keyBindings) {
        return this.focusSource.getIngredientUnderMouse(input, keyBindings).findFirst().flatMap(clicked -> {
            if (input.isSimulate() || this.bookmarkList.onElementBookmarked(clicked.getElement(), input, this.bookmarkOverlay)) {
                SameElementInputHandler handler = new SameElementInputHandler(this, clicked::isMouseOver);
                return Optional.of(handler);
            }
            return Optional.empty();
        });
    }
}

