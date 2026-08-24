package top.theillusivec4.curios.common.network.client;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CPacketDestroy() implements CustomPacketPayload {
   public static final Type<CPacketDestroy> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("curios", "destroy"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CPacketDestroy> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, CPacketDestroy>() {
      @Nonnull
      public CPacketDestroy decode(@Nonnull RegistryFriendlyByteBuf p_320376_) {
         return new CPacketDestroy();
      }

      public void encode(@Nonnull RegistryFriendlyByteBuf p_320158_, @Nonnull CPacketDestroy p_320396_) {
      }
   };

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
