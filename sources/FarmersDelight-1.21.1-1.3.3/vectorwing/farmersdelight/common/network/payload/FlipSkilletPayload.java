package vectorwing.farmersdelight.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FlipSkilletPayload() implements CustomPacketPayload {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "flip_skillet");
   public static final FlipSkilletPayload INSTANCE = new FlipSkilletPayload();
   public static final Type<FlipSkilletPayload> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, FlipSkilletPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
