package dev.isxander.yacl3.config.v3;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface EntryAddable {
   <T> ConfigEntry<T> register(String var1, T var2, Codec<T> var3);

   <T extends CodecConfig<T>> ReadonlyConfigEntry<T> register(String var1, T var2);
}
