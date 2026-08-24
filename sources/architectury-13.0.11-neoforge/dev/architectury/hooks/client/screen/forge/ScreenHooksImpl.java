package dev.architectury.hooks.client.screen.forge;

import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

public class ScreenHooksImpl {
   public static List<NarratableEntry> getNarratables(Screen screen) {
      return screen.narratables;
   }

   public static List<Renderable> getRenderables(Screen screen) {
      return screen.renderables;
   }

   public static <T extends AbstractWidget & Renderable & NarratableEntry> T addRenderableWidget(Screen screen, T widget) {
      return (T)screen.addRenderableWidget(widget);
   }

   public static <T extends Renderable> T addRenderableOnly(Screen screen, T listener) {
      return (T)screen.addRenderableOnly(listener);
   }

   public static <T extends GuiEventListener & NarratableEntry> T addWidget(Screen screen, T listener) {
      return (T)screen.addWidget(listener);
   }
}
