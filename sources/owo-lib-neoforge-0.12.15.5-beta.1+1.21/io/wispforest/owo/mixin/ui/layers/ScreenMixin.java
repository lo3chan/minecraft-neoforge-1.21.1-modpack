package io.wispforest.owo.mixin.ui.layers;

import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   value = {Screen.class},
   priority = 1100
)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements OwoScreenExtension {
   @Shadow
   public int width;
   @Shadow
   public int height;
   private final List<Layer<?, ?>.Instance> owo$instances = new ArrayList<>();
   private final List<Layer<?, ?>.Instance> owo$instancesView = Collections.unmodifiableList(this.owo$instances);
   private final Map<Layer<?, ?>, Layer<?, ?>.Instance> owo$layersToInstances = new HashMap<>();
   private boolean owo$layersInitialized = false;

   private Screen owo$this() {
      return (Screen)this;
   }

   @Override
   public void owo$updateLayers() {
      if (this.owo$layersInitialized) {
         for (Layer<?, ?>.Instance instance : this.owo$instances) {
            instance.resize(this.width, this.height);
         }
      } else {
         for (Layer<Screen, ?> layer : Layers.getLayers(this.owo$this().getClass())) {
            Layer<Screen, ?>.Instance instance = layer.instantiate(this.owo$this());
            this.owo$instances.add(instance);
            this.owo$layersToInstances.put(layer, instance);
            instance.adapter.inflateAndMount();
         }

         this.owo$layersInitialized = true;
      }

      this.owo$instances.forEach(Layer.Instance::dispatchLayoutUpdates);
   }

   @Override
   public <S extends Screen, R extends ParentComponent> Layer<S, R>.Instance owo$getInstance(Layer<S, R> layer) {
      return this.owo$layersToInstances.get(layer);
   }

   @Override
   public List<Layer<?, ?>.Instance> owo$getInstancesView() {
      return this.owo$instancesView;
   }
}
