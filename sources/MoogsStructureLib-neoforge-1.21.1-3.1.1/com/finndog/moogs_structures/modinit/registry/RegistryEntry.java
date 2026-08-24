package com.finndog.moogs_structures.modinit.registry;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public interface RegistryEntry<T> extends Supplier<T> {
   @Override
   T get();

   ResourceLocation getId();
}
