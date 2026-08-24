package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CSetGlobalTimerPayload(long time) implements CustomPacketPayload {
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CSetGlobalTimerPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_LONG, S2CSetGlobalTimerPayload::time, S2CSetGlobalTimerPayload::new
   );
   public static final Type<S2CSetGlobalTimerPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/set_global_timer"));

   public void handleDataOnMain(IPayloadContext context) {
      AlchemancyServerData.setGlobalTimer(this.time);
   }

   public Type<S2CSetGlobalTimerPayload> type() {
      return TYPE;
   }
}
