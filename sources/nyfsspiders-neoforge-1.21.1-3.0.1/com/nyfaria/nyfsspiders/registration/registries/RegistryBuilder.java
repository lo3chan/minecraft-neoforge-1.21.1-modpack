package com.nyfaria.nyfsspiders.registration.registries;

import java.util.function.Supplier;
import net.minecraft.core.Registry;

public interface RegistryBuilder<T> {
   <X> RegistryBuilder<T> withFeature(RegistryFeatureType<X> var1, X var2);

   RegistryBuilder<T> withFeature(RegistryFeatureType<Void> var1);

   RegistryBuilder<T> withDefaultValue(String var1, Supplier<T> var2);

   Registry<T> build();
}
