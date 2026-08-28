/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  java.lang.MatchException
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.bookmarks.BookmarkList;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.elements.IngredientElement;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class ChatLinkInputHandler {
    private final IRecipesGui recipesGui;
    private final FocusUtil focusUtil;
    private final IScreenHelper screenHelper;
    private final BookmarkList bookmarkList;
    @Nullable
    private PendingInput pendingInput;

    public ChatLinkInputHandler(IRecipesGui recipesGui, FocusUtil focusUtil, IScreenHelper screenHelper, BookmarkList bookmarkList) {
        this.recipesGui = recipesGui;
        this.focusUtil = focusUtil;
        this.screenHelper = screenHelper;
        this.bookmarkList = bookmarkList;
    }

    public boolean handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (!(screen instanceof ChatScreen)) {
            this.pendingInput = null;
            return false;
        }
        ChatScreen chatScreen = (ChatScreen)screen;
        return switch (input.getInputType()) {
            default -> throw new MatchException(null, null);
            case InputType.IMMEDIATE -> this.handleImmediateInput(chatScreen, input, keyBindings);
            case InputType.SIMULATE -> this.handleSimulateInput(chatScreen, input, keyBindings);
            case InputType.EXECUTE -> this.handleExecuteInput(chatScreen, input);
        };
    }

    public void handleGuiChange() {
        this.pendingInput = null;
    }

    private boolean handleImmediateInput(ChatScreen chatScreen, UserInput input, IInternalKeyMappings keyBindings) {
        Optional<Action> optionalAction = ChatLinkInputHandler.getAction(input, keyBindings);
        if (optionalAction.isEmpty()) {
            return false;
        }
        Optional<ITypedIngredient<?>> optionalIngredient = this.getHoveredIngredient(chatScreen, input);
        if (optionalIngredient.isEmpty()) {
            return false;
        }
        Action action = optionalAction.get();
        ITypedIngredient<?> typedIngredient = optionalIngredient.get();
        this.executeAction(typedIngredient, action);
        return true;
    }

    private boolean handleSimulateInput(ChatScreen chatScreen, UserInput input, IInternalKeyMappings keyBindings) {
        this.pendingInput = null;
        Optional<Action> optionalAction = ChatLinkInputHandler.getAction(input, keyBindings);
        if (optionalAction.isEmpty()) {
            return false;
        }
        Optional<ITypedIngredient<?>> optionalIngredient = this.getHoveredIngredient(chatScreen, input);
        if (optionalIngredient.isEmpty()) {
            return false;
        }
        Action action = optionalAction.get();
        ITypedIngredient<?> typedIngredient = optionalIngredient.get();
        this.pendingInput = new PendingInput(input.getKey(), typedIngredient, action);
        return true;
    }

    private boolean handleExecuteInput(ChatScreen chatScreen, UserInput input) {
        PendingInput pendingInput = this.pendingInput;
        this.pendingInput = null;
        if (pendingInput == null) {
            return false;
        }
        if (!pendingInput.key().equals((Object)input.getKey())) {
            return false;
        }
        Optional<ITypedIngredient<?>> optionalIngredient = this.getHoveredIngredient(chatScreen, input);
        if (optionalIngredient.isEmpty()) {
            return false;
        }
        ITypedIngredient<?> typedIngredient = optionalIngredient.get();
        if (typedIngredient != pendingInput.typedIngredient()) {
            return false;
        }
        this.executeAction(typedIngredient, pendingInput.action());
        return true;
    }

    private Optional<ITypedIngredient<?>> getHoveredIngredient(ChatScreen chatScreen, UserInput input) {
        return this.screenHelper.getClickableIngredientUnderMouse((Screen)chatScreen, input.getMouseX(), input.getMouseY()).map(ChatLinkInputHandler::getTypedIngredient).findFirst();
    }

    private static ITypedIngredient<?> getTypedIngredient(IClickableIngredient<?> clickableIngredient) {
        return clickableIngredient.getTypedIngredient();
    }

    private static Optional<Action> getAction(UserInput input, IInternalKeyMappings keyBindings) {
        if (input.is(keyBindings.getShowRecipe())) {
            return Optional.of(Action.SHOW_RECIPE);
        }
        if (input.is(keyBindings.getShowUses())) {
            return Optional.of(Action.SHOW_USES);
        }
        if (input.is(keyBindings.getBookmark())) {
            return Optional.of(Action.BOOKMARK);
        }
        return Optional.empty();
    }

    private void executeAction(ITypedIngredient<?> typedIngredient, Action action) {
        switch (action.ordinal()) {
            case 0: {
                this.show(typedIngredient, List.of(RecipeIngredientRole.OUTPUT));
                break;
            }
            case 1: {
                this.show(typedIngredient, List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST));
                break;
            }
            case 2: {
                this.bookmarkList.addIngredientBookmark(typedIngredient);
            }
        }
    }

    private void show(ITypedIngredient<?> typedIngredient, List<RecipeIngredientRole> roles) {
        IngredientElement element = new IngredientElement(typedIngredient);
        element.show(this.recipesGui, this.focusUtil, roles);
    }

    private record PendingInput(InputConstants.Key key, ITypedIngredient<?> typedIngredient, Action action) {
    }

    private static enum Action {
        SHOW_RECIPE,
        SHOW_USES,
        BOOKMARK;

    }
}

