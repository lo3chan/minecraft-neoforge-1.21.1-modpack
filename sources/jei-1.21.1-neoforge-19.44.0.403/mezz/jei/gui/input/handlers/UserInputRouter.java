package mezz.jei.gui.input.handlers;

import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.input.KeyNameUtil;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserInputRouter {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String debugName;
   private final CombinedInputHandler combinedInputHandler;
   private final Map<Key, IUserInputHandler> pending = new HashMap<>();

   public UserInputRouter(String debugName, IUserInputHandler... inputHandlers) {
      this.debugName = debugName;
      this.combinedInputHandler = new CombinedInputHandler(debugName, inputHandlers);
   }

   public boolean handleUserInput(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      if (DebugConfig.isDebugInputsEnabled()) {
         LOGGER.debug("{} received user input: {}", this.debugName, input);
      }
      return switch (input.getInputType()) {
         case IMMEDIATE -> this.handleImmediateClick(screen, input, keyBindings);
         case SIMULATE -> this.handleSimulateClick(screen, input, keyBindings);
         case EXECUTE -> this.handleExecuteClick(screen, input, keyBindings);
      };
   }

   private boolean handleImmediateClick(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      IUserInputHandler oldClick = this.pending.remove(input.getKey());
      if (oldClick != null && DebugConfig.isDebugInputsEnabled()) {
         LOGGER.debug("{} canceled previous user input: {}", this.debugName, oldClick);
      }

      return this.combinedInputHandler.handleUserInput(screen, input, keyBindings).map(callback -> {
         if (DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} immediate click handled by: {}\n{}", this.debugName, callback, input);
         }

         return true;
      }).orElse(false);
   }

   private boolean handleSimulateClick(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      IUserInputHandler oldClick = this.pending.remove(input.getKey());
      if (oldClick != null && DebugConfig.isDebugInputsEnabled()) {
         LOGGER.debug("{} canceled pending user input: {}", this.debugName, oldClick);
      }

      return this.combinedInputHandler.handleUserInput(screen, input, keyBindings).map(callback -> {
         this.pending.put(input.getKey(), callback);
         if (DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} click successfully simulated by: {}\n{}", this.debugName, callback, input);
         }

         return true;
      }).orElse(false);
   }

   private boolean handleExecuteClick(Screen screen, UserInput input, IInternalKeyMappings keyBindings) {
      return Optional.ofNullable(this.pending.remove(input.getKey()))
         .flatMap(inputHandler -> inputHandler.handleUserInput(screen, input, keyBindings))
         .map(callback -> {
            if (DebugConfig.isDebugInputsEnabled()) {
               LOGGER.debug("{} click successfully executed by: {}\n{}", this.debugName, callback, input);
            }

            return true;
         })
         .orElse(false);
   }

   public void handleGuiChange() {
      if (DebugConfig.isDebugInputsEnabled()) {
         LOGGER.debug("{}: The GUI has changed, clearing all pending clicks", this.debugName);
      }

      this.combinedInputHandler.unfocus();
      this.pending.clear();
   }

   public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
      return this.combinedInputHandler.handleMouseScrolled(mouseX, mouseY, scrollDeltaX, scrollDeltaY).map(callback -> {
         if (DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} scroll handled by: {}", this.debugName, callback);
         }

         return true;
      }).orElse(false);
   }

   public boolean handleMouseDragged(double mouseX, double mouseY, Key mouseKey, double dragX, double dragY) {
      Optional<IUserInputHandler> pendingHandler = Optional.ofNullable(this.pending.get(mouseKey))
         .flatMap(inputHandler -> inputHandler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY));
      if (pendingHandler.isPresent()) {
         if (DebugConfig.isDebugInputsEnabled()) {
            LOGGER.debug("{} drag handled by pending handler: {}", this.debugName, pendingHandler.get());
         }

         return true;
      } else {
         return this.combinedInputHandler.handleMouseDragged(mouseX, mouseY, mouseKey, dragX, dragY).map(callback -> {
            if (DebugConfig.isDebugInputsEnabled()) {
               LOGGER.debug("{} drag handled by: {}", this.debugName, callback);
            }

            return true;
         }).orElse(false);
      }
   }

   @Override
   public String toString() {
      String pendingString = this.pending
         .entrySet()
         .stream()
         .map(e -> KeyNameUtil.getKeyDisplayName(e.getKey()) + ": " + e.getValue())
         .collect(Collectors.joining(", ", "[", "]"));
      return "UserInputRouter{debugName='" + this.debugName + "', combinedInputHandler=" + this.combinedInputHandler + ", pending=" + pendingString + "}";
   }
}
