package net.blay09.mods.balm.core;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface BalmRegistrar {
   default <T> Registry<T> createCustomRegistry(ResourceKey<? extends Registry<T>> registryKey) {
      return this.createCustomRegistry(registryKey, (Consumer<CustomRegistryBuilder<T>>)(builder -> {}));
   }

   default <T> Registry<T> createCustomRegistry(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation defaultKey) {
      return this.createCustomRegistry(registryKey, (Consumer<CustomRegistryBuilder<T>>)(builder -> builder.defaultKey(defaultKey)));
   }

   <T> Registry<T> createCustomRegistry(ResourceKey<? extends Registry<T>> var1, Consumer<CustomRegistryBuilder<T>> var2);

   default <T> void createDynamicRegistry(ResourceKey<? extends Registry<T>> registryKey, Codec<T> codec) {
      this.createDynamicRegistry(registryKey, codec, builder -> {});
   }

   <T> void createDynamicRegistry(ResourceKey<? extends Registry<T>> var1, Codec<T> var2, Consumer<DynamicRegistryBuilder<T>> var3);

   default <T> Holder<T> register(ResourceKey<T> resourceKey, Supplier<T> resourceSupplier) {
      return this.register(resourceKey, id -> resourceSupplier.get());
   }

   <T> Holder<T> register(ResourceKey<T> var1, Function<ResourceLocation, T> var2);

   <T> void addAlias(ResourceKey<? extends Registry<T>> var1, ResourceLocation var2, ResourceLocation var3);

   <T> BalmRegistrar.Scoped<T> scoped(ResourceKey<? extends Registry<T>> var1, String var2);

   public interface Scoped<T> {
      Holder<T> register(String var1, Function<ResourceLocation, T> var2);

      void addAlias(ResourceLocation var1, ResourceLocation var2);

      void addAlias(String var1, String var2);
   }
}
