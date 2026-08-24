package fuzs.puzzleslib.api.util.v1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class CodecExtras {
   public static final Codec<NonNullList<ItemStack>> NON_NULL_ITEM_STACK_LIST_CODEC = nonNullList(
      ItemStack.CODEC, Predicate.not(ItemStack::isEmpty), ItemStack.EMPTY
   );
   public static final Codec<ListTag> LIST_TAG_CODEC = Codec.PASSTHROUGH
      .comapFlatMap(
         dynamic -> {
            Tag tag = (Tag)dynamic.convert(NbtOps.INSTANCE).getValue();
            return tag instanceof ListTag listTag
               ? DataResult.success(listTag == dynamic.getValue() ? listTag.copy() : listTag)
               : DataResult.error(() -> "Not a list tag: " + tag);
         },
         listTag -> new Dynamic(NbtOps.INSTANCE, listTag.copy())
      );
   public static final Codec<Integer> RGB_COLOR_CODEC = Codec.withAlternative(
      Codec.INT, ExtraCodecs.VECTOR3F, v -> ARGB.colorFromFloat(1.0F, v.x(), v.y(), v.z())
   );
   public static final Codec<Integer> ARGB_COLOR_CODEC = Codec.withAlternative(
      Codec.INT, ExtraCodecs.VECTOR4F, v -> ARGB.colorFromFloat(v.w(), v.x(), v.y(), v.z())
   );

   private CodecExtras() {
   }

   public static <T> Codec<NonNullList<T>> nonNullList(Codec<T> codec, Predicate<T> filter, @Nullable T defaultValue) {
      return RecordCodecBuilder.create(
         instance -> instance.group(
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter(NonNullList::size),
               Codec.mapPair(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("slot"), codec.fieldOf("item"))
                  .codec()
                  .listOf()
                  .fieldOf("items")
                  .forGetter(
                     items -> IntStream.range(0, items.size())
                        .mapToObj(index -> new Pair(index, items.get(index)))
                        .filter(pair -> filter.test((T)pair.getSecond()))
                        .toList()
                  )
            )
            .apply(instance, (size, items) -> {
               NonNullList<T> nonNullList = defaultValue != null ? NonNullList.withSize(size, defaultValue) : NonNullList.createWithCapacity(size);

               for (Pair<Integer, T> pair : items) {
                  nonNullList.set((Integer)pair.getFirst(), pair.getSecond());
               }

               return nonNullList;
            })
      );
   }

   public static Function<Tag, DataResult<CompoundTag>> mapCompoundTag() {
      return tag -> tag instanceof CompoundTag compoundTag ? DataResult.success(compoundTag) : DataResult.error(() -> "Not a compound tag: " + tag);
   }

   public static <T> Codec<Set<T>> setOf(Codec<T> codec) {
      return Codec.list(codec).xmap(ImmutableSet::copyOf, ImmutableList::copyOf);
   }

   public static <K, V> Codec<Map<K, V>> mapOf(Codec<K> keyCodec, Codec<V> valueCodec) {
      return mapOf(keyCodec.fieldOf("key"), valueCodec.fieldOf("value"));
   }

   public static <K, V> Codec<Map<K, V>> mapOf(MapCodec<K> keyCodec, MapCodec<V> valueCodec) {
      return Codec.mapPair(keyCodec, valueCodec)
         .codec()
         .listOf()
         .xmap(
            list -> list.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond)),
            map -> map.entrySet().stream().map(entry -> new Pair(entry.getKey(), entry.getValue())).toList()
         );
   }

   public static <A> Codec<A> encodeOnly(Encoder<A> encoder) {
      return Codec.of(encoder, Codec.unit(() -> {
         throw new UnsupportedOperationException("Cannot decode with encode-only codec! Encoder:" + encoder);
      }), "EncodeOnly[" + encoder + "]");
   }

   public static <A> Codec<A> decodeOnly(Decoder<A> decoder) {
      return Codec.of(Codec.unit(() -> {
         throw new UnsupportedOperationException("Cannot encode with decode-only codec! Decoder:" + decoder);
      }), decoder, "DecodeOnly[" + decoder + "]");
   }

   public static <E extends Enum<E>> Codec<E> fromEnum(Class<E> enumClazz) {
      return fromEnum(enumClazz::getEnumConstants);
   }

   public static <E extends Enum<E>> Codec<E> fromEnum(Supplier<E[]> enumValues) {
      return fromEnumWithMapping(enumValues, enumConstant -> enumConstant.name().toLowerCase(Locale.ROOT));
   }

   public static <E extends Enum<E>> Codec<E> fromEnumWithMapping(Supplier<E[]> enumValues, Function<E, String> keyFunction) {
      E[] enums = (E[])enumValues.get();
      Function<String, E> function = Arrays.stream(enums).collect(ImmutableMap.toImmutableMap(keyFunction, Function.identity()))::get;
      return Codec.stringResolver(keyFunction, function);
   }

   public static <I, E> Codec<E> idResolverCodec(Codec<I> value, Function<I, E> fromId, Function<E, I> toId) {
      return value.flatXmap(id -> {
         E element = fromId.apply((I)id);
         return element == null ? DataResult.error(() -> "Unknown element id: " + id) : DataResult.success(element);
      }, e -> {
         I id = toId.apply((E)e);
         return id == null ? DataResult.error(() -> "Element with unknown id: " + e) : DataResult.success(id);
      });
   }
}
