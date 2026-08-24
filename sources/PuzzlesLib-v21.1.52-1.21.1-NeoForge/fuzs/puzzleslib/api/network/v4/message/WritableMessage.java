package fuzs.puzzleslib.api.network.v4.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;

public interface WritableMessage<B extends FriendlyByteBuf> {
   static <B extends FriendlyByteBuf, V extends WritableMessage<B>> StreamCodec<B, V> streamCodec(StreamDecoder<B, V> streamDecoder) {
      return StreamCodec.ofMember(WritableMessage::write, streamDecoder);
   }

   void write(B var1);
}
