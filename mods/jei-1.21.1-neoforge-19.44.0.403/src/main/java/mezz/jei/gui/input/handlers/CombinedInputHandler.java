/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  java.lang.MatchException
 *  net.minecraft.client.gui.screens.Screen
 */
package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;

public class CombinedInputHandler
implements IUserInputHandler {
    private final String debugName;
    private final List<IUserInputHandler> inputHandlers;

    public CombinedInputHandler(String debugName, IUserInputHandler ... inputHandlers) {
        this.debugName = debugName;
        this.inputHandlers = List.of(inputHandlers);
    }

    public CombinedInputHandler(String debugName, List<IUserInputHandler> inputHandlers) {
        this.debugName = debugName;
        this.inputHandlers = List.copyOf(inputHandlers);
    }

    @Override
    public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        return switch (input.getInputType()) {
            default -> throw new MatchException(null, null);
            case InputType.IMMEDIATE, InputType.SIMULATE -> this.handleClickInternal(screen, input, keyBindings);
            case InputType.EXECUTE -> Optional.empty();
        };
    }

    private Optional<IUserInputHandler> handleClickInternal(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        return CombinedInputHandler.handleClickInternal(this.inputHandlers, inputHandler -> inputHandler.handleUserInput(screen, input, keyBindings));
    }

    static Optional<IUserInputHandler> handleClickInternal(List<IUserInputHandler> inputHandlers, Function<IUserInputHandler, Optional<IUserInputHandler>> handleInput) {
        Optional<IUserInputHandler> firstHandled = Optional.empty();
        for (IUserInputHandler inputHandler : inputHandlers) {
            if (firstHandled.isEmpty()) {
                firstHandled = handleInput.apply(inputHandler);
                if (!firstHandled.isEmpty()) continue;
                inputHandler.unfocus();
                continue;
            }
            inputHandler.unfocus();
        }
        return firstHandled;
    }

    @Override
    public void unfocus() {
        for (IUserInputHandler inputHandler : this.inputHandlers) {
            inputHandler.unfocus();
        }
    }

    @Override
    public Optional<IUserInputHandler> handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return this.inputHandlers.stream().flatMap(inputHandler -> inputHandler.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY).stream()).findFirst();
    }

    @Override
    public Optional<IUserInputHandler> handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        return this.inputHandlers.stream().flatMap(inputHandler -> inputHandler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY).stream()).findFirst();
    }

    public String toString() {
        String inputHandlersString = this.inputHandlers.stream().map((Function<IUserInputHandler, String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, toString(), (Lmezz/jei/gui/input/IUserInputHandler;)Ljava/lang/String;)()).collect(Collectors.joining(", ", "[", "]"));
        return "CombinedInputHandler{name=" + this.debugName + " inputHandlers=" + inputHandlersString + "}";
    }
}

