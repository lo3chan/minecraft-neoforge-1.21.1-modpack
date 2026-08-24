package top.theillusivec4.curios.common.network.server;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record SPacketBreak(int entityId, String curioId, int slotId) implements CustomPacketPayload {
   public static final Type<SPacketBreak> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("curios", "break"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SPacketBreak> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, SPacketBreak::entityId, ByteBufCodecs.STRING_UTF8, SPacketBreak::curioId, ByteBufCodecs.INT, SPacketBreak::slotId, SPacketBreak::new
   );

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
