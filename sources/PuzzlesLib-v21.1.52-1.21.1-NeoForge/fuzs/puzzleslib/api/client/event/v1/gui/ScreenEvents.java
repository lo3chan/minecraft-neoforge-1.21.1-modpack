package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenEvents {
   private ScreenEvents() {
   }

   public static <T extends Screen> EventInvoker<ScreenEvents.BeforeInit<T>> beforeInit(Class<T> screen) {
      Objects.requireNonNull(screen, "screen type is null");
      return EventInvoker.lookup(ScreenEvents.BeforeInit.class, screen);
   }

   public static <T extends Screen> EventInvoker<ScreenEvents.AfterInit<T>> afterInit(Class<T> screen) {
      Objects.requireNonNull(screen, "screen type is null");
      return EventInvoker.lookup(ScreenEvents.AfterInit.class, screen);
   }

   public static <T extends Screen> EventInvoker<ScreenEvents.Remove<T>> remove(Class<T> screen) {
      Objects.requireNonNull(screen, "screen type is null");
      return EventInvoker.lookup(ScreenEvents.Remove.class, screen);
   }

   public static <T extends Screen> EventInvoker<ScreenEvents.BeforeRender<T>> beforeRender(Class<T> screen) {
      Objects.requireNonNull(screen, "screen type is null");
      return EventInvoker.lookup(ScreenEvents.BeforeRender.class, screen);
   }

   public static <T extends Screen> EventInvoker<ScreenEvents.AfterRender<T>> afterRender(Class<T> screen) {
      Objects.requireNonNull(screen, "screen type is null");
      return EventInvoker.lookup(ScreenEvents.AfterRender.class, screen);
   }

   @FunctionalInterface
   public interface AfterInit<T extends Screen> {
      void onAfterInit(Minecraft var1, T var2, int var3, int var4, List<AbstractWidget> var5, UnaryOperator<AbstractWidget> var6, Consumer<AbstractWidget> var7);
   }

   @FunctionalInterface
   public interface AfterRender<T extends Screen> {
      void onAfterRender(T var1, GuiGraphics var2, int var3, int var4, float var5);
   }

   @FunctionalInterface
   public interface BeforeInit<T extends Screen> {
      void onBeforeInit(Minecraft var1, T var2, int var3, int var4, List<AbstractWidget> var5);
   }

   @FunctionalInterface
   public interface BeforeRender<T extends Screen> {
      void onBeforeRender(T var1, GuiGraphics var2, int var3, int var4, float var5);
   }

   @FunctionalInterface
   public interface Remove<T extends Screen> {
      void onRemove(T var1);
   }
}
