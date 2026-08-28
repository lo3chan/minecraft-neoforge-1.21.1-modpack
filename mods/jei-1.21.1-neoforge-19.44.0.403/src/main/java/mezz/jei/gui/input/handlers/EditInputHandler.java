/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import java.util.Optional;
import java.util.Set;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.CombinedRecipeFocusSource;
import mezz.jei.gui.input.IClickableIngredientInternal;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.SameElementInputHandler;
import net.minecraft.client.gui.screens.Screen;

public class EditInputHandler
implements IUserInputHandler {
    private final CombinedRecipeFocusSource focusSource;
    private final IClientToggleState toggleState;
    private final IEditModeConfig editModeConfig;

    public EditInputHandler(CombinedRecipeFocusSource focusSource, IClientToggleState toggleState, IEditModeConfig editModeConfig) {
        this.focusSource = focusSource;
        this.toggleState = toggleState;
        this.editModeConfig = editModeConfig;
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (!this.toggleState.isEditModeEnabled()) {
            return Optional.empty();
        }
        if (input.is(keyBindings.getToggleHideIngredient())) {
            return this.handle(input, keyBindings, IEditModeConfig.HideMode.SINGLE);
        }
        if (input.is(keyBindings.getToggleWildcardHideIngredient())) {
            return this.handle(input, keyBindings, IEditModeConfig.HideMode.WILDCARD);
        }
        return Optional.empty();
    }

    private Optional<IUserInputHandler> handle(UserInput input, IInternalKeyMappings keyBindings, IEditModeConfig.HideMode hideMode) {
        return this.focusSource.getIngredientUnderMouse(input, keyBindings).findFirst().map(clicked -> {
            if (!input.isSimulate()) {
                this.execute((IClickableIngredientInternal)clicked, hideMode);
            }
            return new SameElementInputHandler(this, clicked::isMouseOver);
        });
    }

    private <V> void execute(IClickableIngredientInternal<V> clicked, IEditModeConfig.HideMode hideMode) {
        ITypedIngredient<V> typedIngredient = clicked.getTypedIngredient();
        Set<IEditModeConfig.HideMode> hideModes = this.editModeConfig.getIngredientHiddenUsingConfigFile(typedIngredient);
        if (hideModes.contains((Object)hideMode)) {
            this.editModeConfig.showIngredientUsingConfigFile(typedIngredient, hideMode);
        } else {
            this.editModeConfig.hideIngredientUsingConfigFile(typedIngredient, hideMode);
        }
    }
}

