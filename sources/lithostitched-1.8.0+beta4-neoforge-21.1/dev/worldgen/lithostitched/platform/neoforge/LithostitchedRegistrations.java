package dev.worldgen.lithostitched.platform.neoforge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

public final class LithostitchedRegistrations {
   public static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTER_CACHE = new HashMap<>();
   public static final List<Consumer<NewRegistry>> DYNAMIC_REGISTRIES = new ArrayList<>();

   public static <T> DeferredRegister<T> createDeferredRegister(ResourceKey<Registry<T>> key) {
      DeferredRegister<T> register = DeferredRegister.create(key, "lithostitched");
      REGISTER_CACHE.put(key, register);
      return register;
   }

   public static void init(IEventBus bus) {
      REGISTER_CACHE.values().forEach(deferredRegistry -> deferredRegistry.register(bus));
      bus.addListener(event -> DYNAMIC_REGISTRIES.forEach(consumer -> consumer.accept(event)));
   }
}
