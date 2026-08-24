package io.wispforest.owo.mixin.text;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.wispforest.owo.text.CustomTextRegistry;
import java.util.stream.Stream;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentContents.Type;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ComponentSerialization.class})
public abstract class TextCodecsMixin {
   @ModifyVariable(
      method = {"createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"},
      at = @At(
         value = "STORE",
         ordinal = 0
      )
   )
   private static <T extends StringRepresentable> Codec<T> injectCustomTextTypesExplicit(Codec<T> codec, T[] types) {
      if (!types.getClass().getComponentType().isAssignableFrom(Type.class)) {
         return codec;
      } else {
         final Codec<T> customTextTypeCodec = Codec.stringResolver(StringRepresentable::getSerializedName, s -> CustomTextRegistry.typesMap().get(s).type());
         return new Codec<T>() {
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
               DataResult<Pair<T, T1>> vanillaResult = codec.decode(ops, input);
               return vanillaResult.result().isPresent() ? vanillaResult : customTextTypeCodec.decode(ops, input);
            }

            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
               DataResult<T1> vanillaResult = codec.encode(input, ops, prefix);
               return vanillaResult.result().isPresent() ? vanillaResult : customTextTypeCodec.encode(input, ops, prefix);
            }
         };
      }
   }

   @ModifyVariable(
      method = {"createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"},
      at = @At(
         value = "STORE",
         ordinal = 0
      )
   )
   private static <T extends StringRepresentable, E> MapCodec<E> injectCustomTextTypesFuzzy(MapCodec<E> codec, T[] types) {
      return !types.getClass().getComponentType().isAssignableFrom(Type.class) ? codec : new MapCodec<E>() {
         public <T1> DataResult<E> decode(DynamicOps<T1> ops, MapLike<T1> input) {
            DataResult<E> vanillaResult = codec.decode(ops, input);
            if (vanillaResult.result().isPresent()) {
               return vanillaResult;
            } else {
               for (CustomTextRegistry.Entry<?> entry : CustomTextRegistry.typesMap().values()) {
                  if (input.get(entry.triggerField()) != null) {
                     return entry.type().codec().decode(ops, input);
                  }
               }

               return vanillaResult;
            }
         }

         public <T1> RecordBuilder<T1> encode(E input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
            return codec.encode(input, ops, prefix);
         }

         public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
            return Stream.concat(codec.keys(ops), CustomTextRegistry.typesMap().values().stream().flatMap(entry -> entry.type().codec().keys(ops)));
         }
      };
   }
}
