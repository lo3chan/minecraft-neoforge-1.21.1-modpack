package dev.shadowsoffire.placebo.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OptionalStackCodec {
   public static final Codec<ItemStack> INSTANCE = Codec.lazyInitialized(
      () -> RecordCodecBuilder.create(
         inst -> inst.group(
               new OptionalStackCodec.OptionalItemMapCodec().forGetter(ItemStack::getItemHolder),
               ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
               DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
            )
            .apply(inst, ItemStack::new)
      )
   );

   private static class OptionalItemMapCodec extends MapCodec<Holder<Item>> {
      private final MapCodec<Holder<Item>> encoder = BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("id");
      private final MapCodec<ResourceLocation> idDecoder = ResourceLocation.CODEC.fieldOf("id");
      private final MapCodec<Boolean> optDecoder = Codec.BOOL.optionalFieldOf("optional", false);

      public <T> DataResult<Holder<Item>> decode(DynamicOps<T> ops, MapLike<T> input) {
         ResourceLocation id = (ResourceLocation)this.idDecoder.decode(ops, input).getOrThrow();
         boolean optional = (Boolean)this.optDecoder.decode(ops, input).getOrThrow();
         Optional<Reference<Item>> item = BuiltInRegistries.ITEM.getHolder(id);
         return !optional && item.isEmpty()
            ? DataResult.error(() -> "Failed to read non-optional item id " + id)
            : DataResult.success(item.map(Function.identity()).orElse(BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR)));
      }

      public <T> RecordBuilder<T> encode(Holder<Item> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
         return this.encoder.encode(input, ops, prefix);
      }

      public <T> Stream<T> keys(DynamicOps<T> ops) {
         return Stream.of((T[])(new Object[]{ops.createString("id"), ops.createString("optional")}));
      }
   }
}
