package dev.latvian.mods.kubejs.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import java.time.Duration;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import org.jetbrains.annotations.Nullable;

public interface KubeJSStreamCodecs {
   StreamCodec<? super RegistryFriendlyByteBuf, IntProvider> INT_PROVIDER = ByteBufCodecs.fromCodecWithRegistries(IntProvider.CODEC);
   StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> KUBEJS_ID = new StreamCodec<RegistryFriendlyByteBuf, ResourceLocation>() {
      public ResourceLocation decode(RegistryFriendlyByteBuf buf) {
         String str = Utf8String.read(buf, 32767);
         return str.indexOf(58) == -1 ? KubeJS.id(str) : ResourceLocation.parse(str);
      }

      public void encode(RegistryFriendlyByteBuf buf, ResourceLocation value) {
         Utf8String.write(buf, value.getNamespace().equals("kubejs") ? value.getPath() : value.toString(), 32767);
      }
   };
   StreamCodec<ByteBuf, JsonElement> JSON_ELEMENT = new StreamCodec<ByteBuf, JsonElement>() {
      public JsonElement decode(ByteBuf buffer) {
         String str = Utf8String.read(buffer, 2147483647);
         return (JsonElement)(!str.isEmpty() && !str.equals("null") ? JsonUtils.fromString(str) : JsonNull.INSTANCE);
      }

      public void encode(ByteBuf buffer, @Nullable JsonElement value) {
         if (value != null && !(value instanceof JsonNull)) {
            Utf8String.write(buffer, JsonUtils.toString(value), 2147483647);
         } else {
            Utf8String.write(buffer, "", 2147483647);
         }
      }
   };
   StreamCodec<ByteBuf, Duration> DURATION = ByteBufCodecs.VAR_LONG.map(Duration::ofMillis, Duration::toMillis);

   static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
      StreamCodec<? super B, T1> codec1,
      Function<C, T1> getter1,
      StreamCodec<? super B, T2> codec2,
      Function<C, T2> getter2,
      StreamCodec<? super B, T3> codec3,
      Function<C, T3> getter3,
      StreamCodec<? super B, T4> codec4,
      Function<C, T4> getter4,
      StreamCodec<? super B, T5> codec5,
      Function<C, T5> getter5,
      StreamCodec<? super B, T6> codec6,
      Function<C, T6> getter6,
      StreamCodec<? super B, T7> codec7,
      Function<C, T7> getter7,
      StreamCodec<? super B, T8> codec8,
      Function<C, T8> getter8,
      Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> func
   ) {
      return new StreamCodec<B, C>() {
         public C decode(B buf) {
            return (C)func.apply(
               codec1.decode(buf),
               codec2.decode(buf),
               codec3.decode(buf),
               codec4.decode(buf),
               codec5.decode(buf),
               codec6.decode(buf),
               codec7.decode(buf),
               codec8.decode(buf)
            );
         }

         public void encode(B buf, C value) {
            codec1.encode(buf, getter1.apply(value));
            codec2.encode(buf, getter2.apply(value));
            codec3.encode(buf, getter3.apply(value));
            codec4.encode(buf, getter4.apply(value));
            codec5.encode(buf, getter5.apply(value));
            codec6.encode(buf, getter6.apply(value));
            codec7.encode(buf, getter7.apply(value));
            codec8.encode(buf, getter8.apply(value));
         }
      };
   }

   static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(
      StreamCodec<? super B, T1> codec1,
      Function<C, T1> getter1,
      StreamCodec<? super B, T2> codec2,
      Function<C, T2> getter2,
      StreamCodec<? super B, T3> codec3,
      Function<C, T3> getter3,
      StreamCodec<? super B, T4> codec4,
      Function<C, T4> getter4,
      StreamCodec<? super B, T5> codec5,
      Function<C, T5> getter5,
      StreamCodec<? super B, T6> codec6,
      Function<C, T6> getter6,
      StreamCodec<? super B, T7> codec7,
      Function<C, T7> getter7,
      StreamCodec<? super B, T8> codec8,
      Function<C, T8> getter8,
      StreamCodec<? super B, T9> codec9,
      Function<C, T9> getter9,
      Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> func
   ) {
      return new StreamCodec<B, C>() {
         public C decode(B buf) {
            return (C)func.apply(
               codec1.decode(buf),
               codec2.decode(buf),
               codec3.decode(buf),
               codec4.decode(buf),
               codec5.decode(buf),
               codec6.decode(buf),
               codec7.decode(buf),
               codec8.decode(buf),
               codec9.decode(buf)
            );
         }

         public void encode(B buf, C value) {
            codec1.encode(buf, getter1.apply(value));
            codec2.encode(buf, getter2.apply(value));
            codec3.encode(buf, getter3.apply(value));
            codec4.encode(buf, getter4.apply(value));
            codec5.encode(buf, getter5.apply(value));
            codec6.encode(buf, getter6.apply(value));
            codec7.encode(buf, getter7.apply(value));
            codec8.encode(buf, getter8.apply(value));
            codec9.encode(buf, getter9.apply(value));
         }
      };
   }
}
