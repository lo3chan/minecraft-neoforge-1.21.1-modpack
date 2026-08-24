package dev.shadowsoffire.placebo.codec;

import com.mojang.serialization.Codec;

public interface CodecProvider<R> {
   Codec<? extends R> getCodec();
}
