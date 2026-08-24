package top.theillusivec4.curios.common.network.server.sync;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record SPacketSyncRender(int entityId, String curioId, int slotId, boolean value) implements CustomPacketPayload {
   public static final Type<SPacketSyncRender> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("curios", "sync_render"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SPacketSyncRender> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      SPacketSyncRender::entityId,
      ByteBufCodecs.STRING_UTF8,
      SPacketSyncRender::curioId,
      ByteBufCodecs.INT,
      SPacketSyncRender::slotId,
      ByteBufCodecs.BOOL,
      SPacketSyncRender::value,
      SPacketSyncRender::new
   );

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
