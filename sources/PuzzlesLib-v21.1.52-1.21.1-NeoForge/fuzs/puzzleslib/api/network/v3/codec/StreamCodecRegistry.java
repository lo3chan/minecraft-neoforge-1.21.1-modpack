package fuzs.puzzleslib.api.network.v3.codec;

import io.netty.buffer.ByteBuf;
import java.lang.reflect.Type;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceKey;

public interface StreamCodecRegistry<T extends StreamCodecRegistry<T>> {
   default <B extends ByteBuf, V> T registerSerializer(Class<V> type, StreamEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
      return this.registerSerializer(type, StreamCodec.of(encoder, decoder));
   }

   default <V> T registerSerializer(Class<? super V> type, ResourceKey<Registry<V>> resourceKey) {
      return this.registerSerializer(type, ByteBufCodecs.registry(resourceKey));
   }

   <B extends ByteBuf, V> T registerSerializer(Class<V> var1, StreamCodec<? super B, V> var2);

   <B extends ByteBuf, V> T registerContainerProvider(Class<V> var1, Function<Type[], StreamCodec<? super B, ? extends V>> var2);
}
