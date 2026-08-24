package net.cibernet.alchemancy.properties.data.modifiers;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Supplier;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record PropertyModifierType<T>(T defaultValue, Codec<T> codec, StreamCodec<? super ByteBuf, T> streamCodec) {
   public static final RegistryFixedCodec<PropertyModifierType<?>> CODEC = RegistryFixedCodec.create(AlchemancyProperties.Modifiers.REGISTRY.getRegistryKey());
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PropertyModifierType<?>>> STREAM_CODEC = ByteBufCodecs.holderRegistry(
      AlchemancyProperties.Modifiers.REGISTRY.getRegistryKey()
   );

   public static <T> Supplier<PropertyModifierType<T>> build(T defaultValue, Codec<T> codec, StreamCodec<? super ByteBuf, T> streamCodec) {
      return () -> new PropertyModifierType<>(defaultValue, codec, streamCodec);
   }

   public void encode(RegistryFriendlyByteBuf encode, Object value) {
      this.streamCodec.encode(encode, value);
   }
}
