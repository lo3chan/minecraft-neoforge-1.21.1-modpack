package com.nyfaria.nyfsspiders.registration;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegistryObject<R, T extends R> extends Supplier<T> {
   ResourceKey<R> getResourceKey();

   ResourceLocation getId();

   @Override
   T get();

   Holder<R> asHolder();
}
