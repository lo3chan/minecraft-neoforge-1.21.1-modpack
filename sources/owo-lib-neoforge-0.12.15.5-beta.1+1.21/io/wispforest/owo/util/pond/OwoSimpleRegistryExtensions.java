package io.wispforest.owo.util.pond;

import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface OwoSimpleRegistryExtensions<T> {
   @Internal
   Reference<T> owo$set(int var1, ResourceKey<T> var2, T var3, RegistrationInfo var4);
}
