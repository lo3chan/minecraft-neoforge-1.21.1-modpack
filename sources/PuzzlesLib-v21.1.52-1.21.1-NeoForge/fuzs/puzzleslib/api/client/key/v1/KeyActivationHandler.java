package fuzs.puzzleslib.api.client.key.v1;

import fuzs.puzzleslib.impl.client.key.KeyActivationHandlerImpl;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface KeyActivationHandler {
   static KeyActivationHandler direct(KeyActivationContext activationContext) {
      return () -> activationContext;
   }

   static KeyActivationHandler of() {
      return new KeyActivationHandlerImpl(null, null, null);
   }

   static KeyActivationHandler forGame(Consumer<Minecraft> gameHandler) {
      Objects.requireNonNull(gameHandler, "game handler is null");
      return new KeyActivationHandlerImpl(gameHandler, null, null);
   }

   static KeyActivationHandler forScreen(Consumer<Screen> screenHandler) {
      return forScreen(Screen.class, screenHandler);
   }

   static <T extends Screen> KeyActivationHandler forScreen(Class<T> screenType, Consumer<T> screenHandler) {
      Objects.requireNonNull(screenType, "screen type is null");
      Objects.requireNonNull(screenHandler, "screen handler is null");
      return new KeyActivationHandlerImpl(null, screenType, screenHandler);
   }

   KeyActivationContext getActivationContext();

   @Nullable
   default Consumer<Minecraft> gameHandler() {
      return null;
   }

   @Nullable
   default Class<? extends Screen> screenType() {
      return null;
   }

   @Nullable
   default Consumer<? extends Screen> screenHandler() {
      return null;
   }

   default KeyActivationHandler withGameHandler(Consumer<Minecraft> gameHandler) {
      return this;
   }

   default KeyActivationHandler withScreenHandler(Consumer<Screen> screenHandler) {
      return this.withScreenHandler(Screen.class, screenHandler);
   }

   default <T extends Screen> KeyActivationHandler withScreenHandler(Class<T> screenType, Consumer<T> screenHandler) {
      return this;
   }
}
