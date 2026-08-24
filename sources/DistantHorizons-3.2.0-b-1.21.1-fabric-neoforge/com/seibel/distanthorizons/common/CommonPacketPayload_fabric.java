package com.seibel.distanthorizons.common;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import net.minecraft.class_8710;
import net.minecraft.class_8710.class_9154;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CommonPacketPayload_fabric(@Nullable AbstractNetworkMessage message) implements class_8710 {
   public static final class_9154<CommonPacketPayload_fabric> TYPE = new class_9154(AbstractPluginPacketSender_fabric.WRAPPER_PACKET_RESOURCE);
   private static final AbstractPluginPacketSender_fabric PACKET_SENDER = (AbstractPluginPacketSender_fabric)SingletonInjector.INSTANCE
      .get(IPluginPacketSender.class);

   @NotNull
   public class_9154<? extends class_8710> method_56479() {
      return TYPE;
   }
}
