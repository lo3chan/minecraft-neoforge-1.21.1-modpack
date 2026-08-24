package dev.corgitaco.enhancedcelestials2core.neoforge.platform;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import dev.corgitaco.enhancedcelestials2core.platform.services.RegistrationService;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry;

@AutoService({RegistrationService.class})
public class NeoForgeRegistrationService implements RegistrationService {
   public static final List<Consumer<NewRegistry>> DATAPACK_REGISTRIES = new ArrayList<>();
   public static final Map<String, Map<ResourceKey<?>, DeferredRegister>> CACHED = new HashMap<>();
   public static final List<Registry<?>> BUILTIN_REGISTRIES = new ArrayList<>();

   @Override
   public <T> Registry<T> createSimpleBuiltin(ResourceKey<Registry<T>> registryKey) {
      Registry<T> registry = new RegistryBuilder(registryKey).sync(true).create();
      BUILTIN_REGISTRIES.add(registry);
      return registry;
   }

   public static void registerBuiltinRegistries(NewRegistryEvent event) {
      BUILTIN_REGISTRIES.forEach(event::register);
   }

   @Override
   public <T> Supplier<T> register(Registry<T> registry, String modId, String location, Supplier<T> value) {
      return CACHED.computeIfAbsent(modId, id -> new Reference2ObjectOpenHashMap())
         .computeIfAbsent(registry.key(), key -> DeferredRegister.create(registry.key().location(), modId))
         .register(location, value);
   }

   @Override
   public <T> void registerDatapackRegistry(ResourceKey<Registry<T>> key, Supplier<Codec<T>> codec) {
      DATAPACK_REGISTRIES.add(newRegistry -> newRegistry.dataPackRegistry(key, codec.get()));
   }
}
