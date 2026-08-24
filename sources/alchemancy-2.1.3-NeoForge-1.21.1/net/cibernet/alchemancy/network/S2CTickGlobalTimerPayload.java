package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.data.save.AlchemancyServerData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class S2CTickGlobalTimerPayload implements CustomPacketPayload {
   public static final S2CTickGlobalTimerPayload INSTANCE = new S2CTickGlobalTimerPayload();
   public static final StreamCodec<RegistryFriendlyByteBuf, S2CTickGlobalTimerPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);
   public static final Type<S2CTickGlobalTimerPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("alchemancy", "s2c/tick_global_timer"));

   public void handleDataOnMain(IPayloadContext context) {
      AlchemancyServerData.tickGlobalTimer();
   }

   public Type<S2CTickGlobalTimerPayload> type() {
      return TYPE;
   }
}
