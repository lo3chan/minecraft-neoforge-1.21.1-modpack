package top.theillusivec4.curios.common.network.client;

import javax.annotation.Nonnull;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CPacketOpenCurios(ItemStack carried) implements CustomPacketPayload {
   public static final Type<CPacketOpenCurios> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("curios", "open_curios"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CPacketOpenCurios> STREAM_CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC, CPacketOpenCurios::carried, CPacketOpenCurios::new
   );

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
