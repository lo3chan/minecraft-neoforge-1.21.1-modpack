/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  java.lang.MatchException
 *  net.minecraft.client.gui.screens.Screen
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.input.KeyNameUtil;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.InputType;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.input.handlers.CombinedInputHandler;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserInputRouter {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String debugName;
    private final CombinedInputHandler combinedInputHandler;
    private final Map<InputConstants.Key, IUserInputHandler> pending = new HashMap<InputConstants.Key, IUserInputHandler>();

    public UserInputRouter(String debugName, IUserInputHandler ... inputHandlers) {
        this.debugName = debugName;
        this.combinedInputHandler = new CombinedInputHandler(debugName, inputHandlers);
    }

    public boolean handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        if (DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} received user input: {}", (Object)this.debugName, (Object)input);
        }
        return switch (input.getInputType()) {
            default -> throw new MatchException(null, null);
            case InputType.IMMEDIATE -> this.handleImmediateClick(screen, input, keyBindings);
            case InputType.SIMULATE -> this.handleSimulateClick(screen, input, keyBindings);
            case InputType.EXECUTE -> this.handleExecuteClick(screen, input, keyBindings);
        };
    }

    private boolean handleImmediateClick(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        IUserInputHandler oldClick = this.pending.remove(input.getKey());
        if (oldClick != null && DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} canceled previous user input: {}", (Object)this.debugName, (Object)oldClick);
        }
        return this.combinedInputHandler.handleUserInput(screen, input, keyBindings).map(callback -> {
            if (DebugConfig.isDebugInputsEnabled()) {
                LOGGER.debug("{} immediate click handled by: {}\n{}", (Object)this.debugName, callback, (Object)input);
            }
            return true;
        }).orElse(false);
    }

    private boolean handleSimulateClick(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        IUserInputHandler oldClick = this.pending.remove(input.getKey());
        if (oldClick != null && DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} canceled pending user input: {}", (Object)this.debugName, (Object)oldClick);
        }
        return this.combinedInputHandler.handleUserInput(screen, input, keyBindings).map(callback -> {
            this.pending.put(input.getKey(), (IUserInputHandler)callback);
            if (DebugConfig.isDebugInputsEnabled()) {
                LOGGER.debug("{} click successfully simulated by: {}\n{}", (Object)this.debugName, callback, (Object)input);
            }
            return true;
        }).orElse(false);
    }

    private boolean handleExecuteClick(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
        return Optional.ofNullable(this.pending.remove(input.getKey())).flatMap(inputHandler -> inputHandler.handleUserInput(screen, input, keyBindings)).map(callback -> {
            if (DebugConfig.isDebugInputsEnabled()) {
                LOGGER.debug("{} click successfully executed by: {}\n{}", (Object)this.debugName, callback, (Object)input);
            }
            return true;
        }).orElse(false);
    }

    public void handleGuiChange() {
        if (DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{}: The GUI has changed, clearing all pending clicks", (Object)this.debugName);
        }
        this.combinedInputHandler.unfocus();
        this.pending.clear();
    }

    public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return this.combinedInputHandler.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY).map(callback -> {
            if (DebugConfig.isDebugInputsEnabled()) {
                LOGGER.debug("{} scroll handled by: {}", (Object)this.debugName, callback);
            }
            return true;
        }).orElse(false);
    }

    public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        Optional pendingHandler = Optional.ofNullable(this.pending.get(mouseKey)).flatMap(inputHandler -> inputHandler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY));
        if (pendingHandler.isPresent()) {
            if (DebugConfig.isDebugInputsEnabled()) {
                LOGGER.debug("{} drag handled by pending handler: {}", (Object)this.debugName, pendingHandler.get());
            }
            return true;
        }
        return this.combinedInputHandler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY).map(callback -> {
            if (DebugConfig.isDebugInputsEnabled()) {
                LOGGER.debug("{} drag handled by: {}", (Object)this.debugName, callback);
            }
            return true;
        }).orElse(false);
    }

    public String toString() {
        String pendingString = this.pending.entrySet().stream().map(e -> String.valueOf(KeyNameUtil.getKeyDisplayName((InputConstants.Key)e.getKey())) + ": " + String.valueOf(e.getValue())).collect(Collectors.joining(", ", "[", "]"));
        return "UserInputRouter{debugName='" + this.debugName + "', combinedInputHandler=" + String.valueOf(this.combinedInputHandler) + ", pending=" + pendingString + "}";
    }
}

