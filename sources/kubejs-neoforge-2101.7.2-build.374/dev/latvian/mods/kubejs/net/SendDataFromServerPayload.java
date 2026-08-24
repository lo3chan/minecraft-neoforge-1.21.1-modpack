package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public record SendDataFromServerPayload(String channel, @Nullable CompoundTag data) implements CustomPacketPayload {
   public static final StreamCodec<ByteBuf, SendDataFromServerPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      SendDataFromServerPayload::channel,
      ByteBufCodecs.COMPOUND_TAG,
      SendDataFromServerPayload::data,
      SendDataFromServerPayload::new
   );

   public SendDataFromServerPayload(String channel, @Nullable CompoundTag data) {
      this.channel = channel;
      this.data = Objects.requireNonNullElseGet(data, CompoundTag::new);
   }

   public Type<?> type() {
      return KubeJSNet.SEND_DATA_FROM_SERVER;
   }

   public void handle(IPayloadContext ctx) {
      if (!this.channel.isEmpty()) {
         ctx.enqueueWork(() -> KubeJS.PROXY.handleDataFromServerPacket(this.channel, this.data));
      }
   }
}
