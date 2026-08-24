package net.cibernet.alchemancy.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CRidePlayerPayload(int riderId, boolean dismount) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CRidePlayerPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, S2CRidePlayerPayload::riderId, ByteBufCodecs.BOOL, S2CRidePlayerPayload::dismount, S2CRidePlayerPayload::new
   );
   public static final Type<S2CRidePlayerPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/ride_player"));

   public void handleDataOnMain(IPayloadContext context) {
      Entity rider = context.player().level().getEntity(this.riderId());
      if (rider != null) {
         if (this.dismount()) {
            rider.stopRiding();
         } else {
            rider.startRiding(context.player());
         }
      }
   }

   public Type<S2CRidePlayerPayload> type() {
      return TYPE;
   }
}
