package io.wispforest.owo.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.callback.AddCallback;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class RegistryHelper<T> {
   private static final Map<Registry<?>, RegistryHelper<?>> INSTANCES = new HashMap<>();
   private final Registry<T> registry;
   private final Map<ResourceLocation, Consumer<T>> actions = new HashMap<>();
   private final List<ComplexRegistryAction> complexActions = new ArrayList<>();

   public static <T> RegistryHelper<T> get(Registry<T> registry) {
      return (RegistryHelper<T>)INSTANCES.computeIfAbsent(registry, objects -> new RegistryHelper(registry));
   }

   @Internal
   public RegistryHelper(Registry<T> registry) {
      this.registry = registry;
      registry.addCallback((AddCallback)(registry1, rawid, id, object) -> {
         if (this.actions.containsKey(id)) {
            this.actions.remove(id).accept((T)object);
         }

         ArrayList<Runnable> actionsToExecute = new ArrayList<>();
         this.complexActions.removeIf(action -> action.update(id.location(), actionsToExecute));
         actionsToExecute.forEach(Runnable::run);
      });
   }

   public void runWhenPresent(ResourceLocation id, Consumer<T> action) {
      if (isContained(this.registry, id)) {
         action.accept((T)this.registry.get(id));
      } else {
         this.actions.put(id, action);
      }
   }

   public void runWhenPresent(ComplexRegistryAction action) {
      if (!action.preCheck(this.registry)) {
         this.complexActions.add(action);
      }
   }

   private static <T> boolean isContained(Registry<T> registry, ResourceLocation identifier) {
      return registry.containsKey(identifier);
   }
}
