package io.wispforest.owo.ui.layers;

import io.wispforest.owo.mixin.ui.layers.HandledScreenAccessor;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class Layer<S extends Screen, R extends ParentComponent> {
   protected final BiFunction<Sizing, Sizing, R> rootComponentMaker;
   protected final Consumer<Layer<S, R>.Instance> instanceInitializer;

   protected Layer(BiFunction<Sizing, Sizing, R> rootComponentMaker, Consumer<Layer<S, R>.Instance> instanceInitializer) {
      this.rootComponentMaker = rootComponentMaker;
      this.instanceInitializer = instanceInitializer;
   }

   public Layer<S, R>.Instance instantiate(S screen) {
      return new Layer.Instance(screen);
   }

   public Layer<S, R>.Instance getInstance(S screen) {
      return ((OwoScreenExtension)screen).owo$getInstance(this);
   }

   public class Instance {
      public final S screen;
      public final OwoUIAdapter<R> adapter;
      public boolean aggressivePositioning = false;
      protected final List<Runnable> layoutUpdaters = new ArrayList<>();

      protected Instance(S screen) {
         this.screen = screen;
         this.adapter = OwoUIAdapter.createWithoutScreen(0, 0, screen.width, screen.height, Layer.this.rootComponentMaker);
         Layer.this.instanceInitializer.accept(this);
      }

      @Internal
      public void resize(int width, int height) {
         this.adapter.moveAndResize(0, 0, width, height);
      }

      @Nullable
      public AbstractWidget queryWidget(Predicate<AbstractWidget> locator) {
         ArrayList<AbstractWidget> widgets = new ArrayList<>();

         for (GuiEventListener element : this.screen.children()) {
            collectChildren(element, widgets);
         }

         AbstractWidget widget = null;

         for (AbstractWidget candidate : widgets) {
            if (locator.test(candidate)) {
               widget = candidate;
               break;
            }
         }

         return widget;
      }

      public void alignComponentToWidget(Predicate<AbstractWidget> locator, Layer.Instance.AnchorSide anchor, float justification, Component component) {
         this.layoutUpdaters
            .add(
               () -> {
                  AbstractWidget widget = this.queryWidget(locator);
                  if (widget == null) {
                     component.positioning(Positioning.absolute(0, 0));
                  } else {
                     Size size = component.fullSize();
                     switch (anchor) {
                        case TOP:
                           component.positioning(
                              Positioning.absolute((int)(widget.getX() + (widget.getWidth() - size.width()) * justification), widget.getY() - size.height())
                           );
                           break;
                        case BOTTOM:
                           component.positioning(
                              Positioning.absolute(
                                 (int)(widget.getX() + (widget.getWidth() - size.width()) * justification), widget.getY() + widget.getHeight()
                              )
                           );
                           break;
                        case LEFT:
                           component.positioning(
                              Positioning.absolute(widget.getX() - size.width(), (int)(widget.getY() + (widget.getHeight() - size.height()) * justification))
                           );
                           break;
                        case RIGHT:
                           component.positioning(
                              Positioning.absolute(
                                 widget.getX() + widget.getWidth(), (int)(widget.getY() + (widget.getHeight() - size.height()) * justification)
                              )
                           );
                     }
                  }
               }
            );
      }

      public void alignComponentToHandledScreenCoordinates(Component component, int x, int y) {
         if (this.screen instanceof AbstractContainerScreen<?> handledScreen) {
            this.layoutUpdaters
               .add(
                  () -> component.positioning(
                     Positioning.absolute(((HandledScreenAccessor)handledScreen).owo$getRootX() + x, ((HandledScreenAccessor)handledScreen).owo$getRootY() + y)
                  )
               );
         } else {
            throw new IllegalStateException("Handled screen coordinates only exist on screens which extend HandledScreen<?>");
         }
      }

      @Internal
      public void dispatchLayoutUpdates() {
         this.layoutUpdaters.forEach(Runnable::run);
      }

      private static void collectChildren(GuiEventListener element, List<AbstractWidget> children) {
         if (element instanceof AbstractWidget widget) {
            children.add(widget);
         }

         if (element instanceof Layout layout) {
            layout.visitWidgets(child -> collectChildren(child, children));
         }
      }

      public static enum AnchorSide {
         TOP,
         BOTTOM,
         LEFT,
         RIGHT;
      }
   }
}
