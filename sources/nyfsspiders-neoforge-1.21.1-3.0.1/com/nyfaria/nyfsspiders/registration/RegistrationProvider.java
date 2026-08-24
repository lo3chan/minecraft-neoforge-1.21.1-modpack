package com.nyfaria.nyfsspiders.registration;

import com.nyfaria.nyfsspiders.registration.registries.RegistryBuilder;
import com.nyfaria.nyfsspiders.registration.specialised.SpecialisedRegistrationFactory;
import com.nyfaria.nyfsspiders.registration.util.$InternalRegUtils;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegistrationProvider<T> {
   static <T> RegistrationProvider<T> get(ResourceKey<? extends Registry<T>> registryKey, String modId) {
      return RegistrationProvider.Factory.INSTANCE.create(registryKey, modId);
   }

   static <T> RegistrationProvider<T> get(ResourceLocation registryId, String modId) {
      return RegistrationProvider.Factory.INSTANCE.create(ResourceKey.createRegistryKey(registryId), modId);
   }

   static <T> RegistrationProvider<T> get(Registry<T> registry, String modId) {
      return RegistrationProvider.Factory.INSTANCE.create(registry, modId);
   }

   <I extends T> RegistryObject<T, I> register(String var1, Supplier<? extends I> var2);

   Collection<RegistryObject<T, ? extends T>> getEntries();

   ResourceKey<? extends Registry<T>> getRegistryKey();

   Registry<T> getRegistry();

   String getModId();

   RegistryBuilder<T> registryBuilder();

   public interface Factory extends SpecialisedRegistrationFactory {
      RegistrationProvider.Factory INSTANCE = $InternalRegUtils.getOneAndOnlyService(RegistrationProvider.Factory.class);

      <T> RegistrationProvider<T> create(ResourceKey<? extends Registry<T>> var1, String var2);

      default <T> RegistrationProvider<T> create(Registry<T> registry, String modId) {
         return this.create(registry.key(), modId);
      }
   }
}
