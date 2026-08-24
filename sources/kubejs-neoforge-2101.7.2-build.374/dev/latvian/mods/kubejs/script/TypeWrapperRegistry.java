package dev.latvian.mods.kubejs.script;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.wrap.DirectTypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperValidator;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public class TypeWrapperRegistry {
   private final ScriptType type;
   private final TypeWrappers typeWrappers;

   public TypeWrapperRegistry(ScriptType type, TypeWrappers typeWrappers) {
      this.type = type;
      this.typeWrappers = typeWrappers;
   }

   public ScriptType scriptType() {
      return this.type;
   }

   public <T> boolean hasTypeWrapper(Class<T> target) {
      return this.typeWrappers.wrappers.containsKey(target);
   }

   public <T> void register(Class<T> target, TypeWrapperValidator validator, TypeWrapperFactory<T> factory) {
      this.typeWrappers.register(target, validator, factory);
   }

   public <T> void register(Class<T> target, TypeWrapperFactory<T> factory) {
      this.typeWrappers.register(target, factory);
   }

   public <T> void register(Class<T> target, TypeWrapperValidator validator, TypeWrapperRegistry.ContextFromFunction<T> factory) {
      this.typeWrappers.register(target, validator, (cx, from, t) -> factory.apply(cx, from));
   }

   public <T> void register(Class<T> target, TypeWrapperRegistry.ContextFromFunction<T> factory) {
      this.typeWrappers.register(target, (cx, from, t) -> factory.apply(cx, from));
   }

   public <T> void register(Class<T> target, TypeWrapperRegistry.RegistriesFromFunction<T> factory) {
      this.typeWrappers.register(target, (cx, from, t) -> {
         try {
            return factory.apply(RegistryAccessContainer.of(cx), from);
         } catch (KubeRuntimeException var5) {
            throw var5.source(SourceLine.of(cx));
         }
      });
   }

   public <T> void register(Class<T> target, TypeWrapperValidator validator, DirectTypeWrapperFactory<T> factory) {
      this.typeWrappers.registerDirect(target, validator, factory);
   }

   public <T> void register(Class<T> target, DirectTypeWrapperFactory<T> factory) {
      this.typeWrappers.registerDirect(target, factory);
   }

   public <T extends Enum<T> & StringRepresentable> void registerEnumFromStringCodec(Class<T> target, Codec<T> codec, T defaultValue, boolean forceLowerCase) {
      this.register(target, (DirectTypeWrapperFactory<T>)(o -> {
         o = Wrapper.unwrapped(o);
         if (o == null) {
            return defaultValue;
         } else if (target.isInstance(o)) {
            return Cast.to(o);
         } else {
            String s = o.toString();
            return s.isEmpty() ? defaultValue : KubeJSCodecs.byName(codec, forceLowerCase ? s.toLowerCase(Locale.ROOT) : s);
         }
      }));
   }

   public <T extends Enum<T> & StringRepresentable> void registerEnumFromStringCodec(Class<T> target, Codec<T> codec) {
      this.registerEnumFromStringCodec(target, codec, null, true);
   }

   public <T> void registerCodec(Class<T> target, Codec<T> codec, T defaultValue) {
      this.typeWrappers.register(target, new CodecTypeWrapper<>(target, codec, defaultValue));
   }

   public <T> void registerCodec(Class<T> target, Codec<T> codec) {
      this.registerCodec(target, codec, null);
   }

   public <T> void registerMapCodec(Class<T> target, MapCodec<T> codec, T defaultValue) {
      this.typeWrappers.register(target, new MapCodecTypeWrapper<>(target, codec, defaultValue));
   }

   public <T> void registerMapCodec(Class<T> target, MapCodec<T> codec) {
      this.registerMapCodec(target, codec, null);
   }

   public <F, T> void registerAlias(Class<T> target, Class<F> from, Function<F, T> converter) {
      this.registerAlias(target, TypeInfo.of(from), converter);
   }

   public <F, T> void registerAlias(Class<T> target, TypeInfo from, @Nullable Function<F, T> converter) {
      if (converter != null) {
         this.typeWrappers.register(target, (cx, f, typeInfo) -> {
            Object o1 = cx.jsToJava(f, from);
            return o1 == null ? null : Cast.to(converter.apply(Cast.to(o1)));
         });
      }
   }

   public interface ContextFromFunction<T> extends BiFunction<Context, Object, T> {
   }

   public interface RegistriesFromFunction<T> extends BiFunction<RegistryAccessContainer, Object, T> {
   }
}
