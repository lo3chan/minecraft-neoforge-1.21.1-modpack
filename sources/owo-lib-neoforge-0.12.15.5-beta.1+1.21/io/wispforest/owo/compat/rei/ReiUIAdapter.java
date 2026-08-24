package io.wispforest.owo.compat.rei;

import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.ScissorStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.common.NeoForge;

public class ReiUIAdapter<T extends ParentComponent> extends Widget {
   public static final Point LAYOUT = new Point(-69, -69);
   public final OwoUIAdapter<T> adapter;
   private static final Map<Screen, Set<OwoUIAdapter<?>>> currentREIAdapters = new HashMap<>();

   public ReiUIAdapter(Rectangle bounds, BiFunction<Sizing, Sizing, T> rootComponentMaker) {
      this.adapter = OwoUIAdapter.createWithoutScreen(bounds.x, bounds.y, bounds.width, bounds.height, rootComponentMaker);
      this.adapter.inspectorZOffset = 900;
      if (Minecraft.getInstance().screen != null) {
         currentREIAdapters.computeIfAbsent(Minecraft.getInstance().screen, screen -> new HashSet<>()).add(this.adapter);
      }
   }

   public void prepare() {
      this.adapter.inflateAndMount();
   }

   public T rootComponent() {
      return this.adapter.rootComponent;
   }

   public <W extends WidgetWithBounds> ReiWidgetComponent wrap(W widget) {
      return new ReiWidgetComponent(widget);
   }

   public <W extends WidgetWithBounds> ReiWidgetComponent wrap(Function<Point, W> widgetFactory, Consumer<W> widgetConfigurator) {
      W widget = (W)widgetFactory.apply(LAYOUT);
      widgetConfigurator.accept(widget);
      return new ReiWidgetComponent(widget);
   }

   public boolean containsMouse(double mouseX, double mouseY) {
      return this.adapter.isMouseOver(mouseX, mouseY);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.adapter.mouseClicked(mouseX - this.adapter.x(), mouseY - this.adapter.y(), button);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.adapter.mouseScrolled(mouseX - this.adapter.x(), mouseY - this.adapter.y(), horizontalAmount, verticalAmount);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return this.adapter.mouseReleased(mouseX - this.adapter.x(), mouseY - this.adapter.y(), button);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.adapter.mouseDragged(mouseX - this.adapter.x(), mouseY - this.adapter.y(), button, deltaX, deltaY);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.adapter.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.adapter.keyReleased(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char chr, int modifiers) {
      return this.adapter.charTyped(chr, modifiers);
   }

   public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
      ScissorStack.push(this.adapter.x(), this.adapter.y(), this.adapter.width(), this.adapter.height(), context.pose());
      this.adapter.render(context, mouseX, mouseY, partialTicks);
      ScissorStack.pop();
      context.flush();
   }

   public List<? extends GuiEventListener> children() {
      return List.of();
   }

   static {
      NeoForge.EVENT_BUS.addListener(event -> {
         Set<OwoUIAdapter<?>> adapters = currentREIAdapters.remove(event.getScreen());
         if (adapters != null) {
            adapters.forEach(OwoUIAdapter::dispose);
         }
      });
   }
}
