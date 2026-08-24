package top.theillusivec4.curios.common.network.client;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CPacketPage(int windowId, boolean next) implements CustomPacketPayload {
   public static final Type<CPacketPage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("curios", "client_page"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CPacketPage> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, CPacketPage::windowId, ByteBufCodecs.BOOL, CPacketPage::next, CPacketPage::new
   );

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
