package dev.shadowsoffire.placebo.codec;

import com.google.common.collect.BiMap;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public class PlaceboCodecs {
   public static <T extends CodecProvider<T>> Codec<T> mapBackedDefaulted(
      String name, BiMap<ResourceLocation, Codec<? extends T>> reg, Codec<? extends T> defaultCodec
   ) {
      return new MapBackedCodec<>(name, reg, () -> defaultCodec);
   }

   public static <T extends CodecProvider<? super T>> Codec<T> mapBacked(String name, BiMap<ResourceLocation, Codec<? extends T>> reg) {
      return new MapBackedCodec<>(name, reg);
   }

   public static <T> Codec<Set<T>> setOf(Codec<T> elementCodec) {
      return setFromList(elementCodec.listOf());
   }

   public static <T> Codec<Set<T>> setFromList(Codec<List<T>> listCodec) {
      return listCodec.xmap(LinkedHashSet::new, ArrayList::new);
   }

   public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> clazz) {
      return Codec.stringResolver(e -> e.name().toLowerCase(Locale.ROOT), name -> Enum.valueOf(clazz, name.toUpperCase(Locale.ROOT)));
   }

   @Deprecated
   public static <E> Codec<E> stringResolverCodec(Function<E, String> to, Function<String, E> from) {
      return Codec.stringResolver(to, from);
   }
}
