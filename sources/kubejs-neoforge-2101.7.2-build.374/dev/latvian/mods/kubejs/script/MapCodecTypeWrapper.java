package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;

public record MapCodecTypeWrapper<T>(Class<T> target, MapCodec<T> codec, T defaultValue) implements TypeWrapperFactory<T> {
   public T wrap(Context cx, Object o, TypeInfo target) {
      o = Wrapper.unwrapped(o);
      if (o == null) {
         return this.defaultValue;
      } else {
         return target.asClass().isInstance(o) ? Cast.to(o) : RegistryAccessContainer.of(cx).decodeMap(cx, this.codec, o);
      }
   }
}
