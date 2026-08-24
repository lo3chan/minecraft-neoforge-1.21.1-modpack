package io.wispforest.owo.ui.layers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

public final class Layers {
   public static final ResourceLocation INIT_PHASE = ResourceLocation.fromNamespaceAndPath("owo", "init-layers");
   private static final Multimap<Class<? extends Screen>, Layer<?, ?>> LAYERS = HashMultimap.create();

   @SafeVarargs
   public static <S extends Screen, R extends ParentComponent> Layer<S, R> add(
      BiFunction<Sizing, Sizing, R> rootComponentMaker, Consumer<Layer<S, R>.Instance> instanceInitializer, Class<? extends S>... screenClasses
   ) {
      Layer<S, R> layer = new Layer<>(rootComponentMaker, instanceInitializer);

      for (Class<? extends S> screenClass : screenClasses) {
         LAYERS.put(screenClass, layer);
      }

      return layer;
   }

   public static <S extends Screen> Collection<Layer<S, ?>> getLayers(Class<S> screenClass) {
      return LAYERS.get(screenClass);
   }

   public static <S extends Screen> List<Layer<S, ?>.Instance> getInstances(S screen) {
      return ((OwoScreenExtension)screen).owo$getInstancesView();
   }

   static {
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> ((OwoScreenExtension)event.getScreen()).owo$updateLayers());
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            instance.adapter.dispose();
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            if (instance.aggressivePositioning) {
               instance.dispatchLayoutUpdates();
            }
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         event.getGuiGraphics().flush();

         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            instance.adapter.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            boolean handled = instance.adapter.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
            if (handled) {
               event.setCanceled(true);
               return;
            }
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            boolean handled = instance.adapter.mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
            if (handled) {
               event.setCanceled(true);
               return;
            }
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            boolean handled = instance.adapter.mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
            if (handled) {
               event.setCanceled(true);
               return;
            }
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            boolean handled = instance.adapter.keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
            if (handled) {
               event.setCanceled(true);
               return;
            }
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.LOW, event -> {
         for (Layer<Screen, ?>.Instance instance : getInstances(event.getScreen())) {
            boolean handled = instance.adapter.keyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers());
            if (handled) {
               event.setCanceled(true);
               return;
            }
         }
      });
   }
}
