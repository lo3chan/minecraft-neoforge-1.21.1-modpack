package net.mehvahdjukaar.moonlight.api.util.codec.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.BaseMapCodec;
import java.util.Map;
import net.neoforged.neoforge.common.LenientUnboundedMapCodec;

public class CodecUtilsImpl {
   public static <K, V, C extends BaseMapCodec<K, V> & Codec<Map<K, V>>> C optionalMapCodec(Codec<K> keyCodec, Codec<V> elementCodec) {
      return (C)(new LenientUnboundedMapCodec(keyCodec, elementCodec));
   }
}
