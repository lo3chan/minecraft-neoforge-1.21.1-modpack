package com.nyfaria.nyfsspiders.registration.registries;

import com.mojang.serialization.Codec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;

@ParametersAreNonnullByDefault
public interface DatapackRegistryBuilder<T> {
   DatapackRegistryBuilder<T> withElementCodec(@Nonnull Codec<T> var1);

   DatapackRegistryBuilder<T> withNetworkCodec(@Nullable Codec<T> var1);

   DatapackRegistryBuilder<T> withBootstrap(@Nullable RegistryBootstrap<T> var1);

   DatapackRegistry<T> build();
}
