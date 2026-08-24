package com.seibel.distanthorizons.common;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CommonPacketPayload_neoforge(@Nullable AbstractNetworkMessage message) implements CustomPacketPayload {
   public static final Type<CommonPacketPayload_neoforge> TYPE = new Type(AbstractPluginPacketSender_neoforge.WRAPPER_PACKET_RESOURCE);
   private static final AbstractPluginPacketSender_neoforge PACKET_SENDER = (AbstractPluginPacketSender_neoforge)SingletonInjector.INSTANCE
      .get(IPluginPacketSender.class);

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
