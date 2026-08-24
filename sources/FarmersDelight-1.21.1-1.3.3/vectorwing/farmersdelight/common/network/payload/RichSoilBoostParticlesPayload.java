package vectorwing.farmersdelight.common.network.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RichSoilBoostParticlesPayload(BlockPos pos) implements CustomPacketPayload {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil_boost_particles");
   public static final Type<RichSoilBoostParticlesPayload> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, RichSoilBoostParticlesPayload> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, RichSoilBoostParticlesPayload::pos, RichSoilBoostParticlesPayload::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
