package dev.latvian.mods.kubejs.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface NameProvider<T> {
   static <K, T> Map<K, NameProvider<T>> create(Consumer<NameProvider.Registry<K, T>> registry) {
      HashMap<K, NameProvider<T>> map = new HashMap<>();
      registry.accept(map::put);
      return map;
   }

   @Nullable
   Component getName(RegistryAccess registries, T value);

   public interface Registry<K, T> {
      void register(K key, NameProvider<T> provider);

      default void register(List<K> keys, NameProvider<T> provider) {
         for (K key : keys) {
            this.register(key, provider);
         }
      }
   }
}
