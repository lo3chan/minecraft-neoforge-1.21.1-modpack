/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.gui.input.handlers;

import java.util.List;
import java.util.Optional;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.common.chat.JeiChatItemLinks;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.SameElementInputHandler;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.util.CommandUtil;
import mezz.jei.gui.util.FocusUtil;
import mezz.jei.gui.util.GiveAmount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;

public class FocusInputHandler
implements IUserInputHandler {
    private final CombinedRecipeFocusSource focusSource;
    private final IRecipesGui recipesGui;
    private final FocusUtil focusUtil;
    private final IIngredientManager ingredientManager;
    private final IClientToggleState toggleState;
    private final CommandUtil commandUtil;

    public FocusInputHandler(CombinedRecipeFocusSource focusSource, IRecipesGui recipesGui, FocusUtil focusUtil, IClientConfig clientConfig, IIngredientManager ingredientManager, IClientToggleState toggleState, IConnectionToServer serverConnection) {
        this.focusSource = focusSource;
        this.recipesGui = recipesGui;
        this.focusUtil = focusUtil;
        this.ingredientManager = ingredientManager;
        this.toggleState = toggleState;
        this.commandUtil = new CommandUtil(clientConfig, serverConnection);
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        Optional<IUserInputHandler> handledClick = this.handleClick(input, keyBindings);
        if (handledClick.isPresent()) {
            return handledClick;
        }
        if (this.toggleState.isCheatItemsEnabled() && screen instanceof AbstractContainerScreen) {
            Optional<IUserInputHandler> handler;
            if (input.is(keyBindings.getCheatItemStack()) && (handler = this.handleGive(input, keyBindings, GiveAmount.MAX)).isPresent()) {
                return handler;
            }
            if (input.is(keyBindings.getCheatOneItem()) && (handler = this.handleGive(input, keyBindings, GiveAmount.ONE)).isPresent()) {
                return handler;
            }
        }
        if (input.is(keyBindings.getShowRecipe())) {
            return this.handleShow(input, List.of(RecipeIngredientRole.OUTPUT), keyBindings);
        }
        if (input.is(keyBindings.getShareToChat())) {
            return this.handleShareToChat(input, keyBindings);
        }
        if (input.is(keyBindings.getShowUses())) {
            return this.handleShow(input, List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.CATALYST), keyBindings);
        }
        return Optional.empty();
    }

    private Optional<IUserInputHandler> handleClick(UserInput input, IInternalKeyMappings keyBindings) {
        List<IClickableIngredientInternal<?>> ingredientUnderMouse = this.focusSource.getIngredientUnderMouse(input, keyBindings).toList();
        for (IClickableIngredientInternal<?> clicked : ingredientUnderMouse) {
            IElement<?> element = clicked.getElement();
            if (!element.handleClick(input, keyBindings)) continue;
            SameElementInputHandler result = new SameElementInputHandler(this, clicked::isMouseOver);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private Optional<IUserInputHandler> handleShow(UserInput input, List<RecipeIngredientRole> roles, IInternalKeyMappings keyBindings) {
        return this.focusSource.getIngredientUnderMouse(input, keyBindings).filter(clicked -> clicked.getElement().isVisible()).findFirst().map(clicked -> {
            if (!input.isSimulate()) {
                clicked.show(this.recipesGui, this.focusUtil, roles);
            }
            return new SameElementInputHandler(this, clicked::isMouseOver);
        });
    }

    private Optional<IUserInputHandler> handleShareToChat(UserInput input, IInternalKeyMappings keyBindings) {
        return this.focusSource.getIngredientUnderMouse(input, keyBindings).filter(clicked -> clicked.getElement().isVisible()).findFirst().map(clicked -> {
            if (!input.isSimulate()) {
                ITypedIngredient typedIngredient = clicked.getTypedIngredient();
                String chatText = JeiChatItemLinks.createLinkMarker(typedIngredient, this.ingredientManager);
                Minecraft minecraft = Minecraft.getInstance();
                ChatScreen chatScreen = new ChatScreen(chatText);
                minecraft.setScreen((Screen)chatScreen);
            }
            return new SameElementInputHandler(this, clicked::isMouseOver);
        });
    }

    private Optional<IUserInputHandler> handleGive(UserInput input, IInternalKeyMappings keyBindings, GiveAmount giveAmount) {
        return this.focusSource.getIngredientUnderMouse(input, keyBindings).mapMulti((clicked, consumer) -> {
            ItemStack itemStack = clicked.getCheatItemStack(this.ingredientManager);
            if (!itemStack.isEmpty()) {
                if (!input.isSimulate()) {
                    this.commandUtil.giveStack(itemStack, giveAmount);
                }
                SameElementInputHandler handler = new SameElementInputHandler(this, clicked::isMouseOver);
                consumer.accept(handler);
            }
        }).findFirst();
    }
}

