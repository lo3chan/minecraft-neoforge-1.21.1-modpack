package at.petrak.hexcasting.common.msgs;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IMessage {
   default FriendlyByteBuf toBuf() {
      FriendlyByteBuf ret = new FriendlyByteBuf(Unpooled.buffer());
      this.serialize(ret);
      return ret;
   }

   void serialize(FriendlyByteBuf var1);

   ResourceLocation id();
}
