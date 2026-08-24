package com.seibel.distanthorizons.common;

import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.network.event.internal.IncompatibleMessageInternalEvent;
import com.seibel.distanthorizons.core.network.event.internal.ProtocolErrorInternalEvent;
import com.seibel.distanthorizons.core.network.messages.AbstractNetworkMessage;
import com.seibel.distanthorizons.core.network.messages.MessageRegistry;
import com.seibel.distanthorizons.core.network.messages.base.CloseReasonMessage;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IPluginPacketSender;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import io.netty.buffer.ByteBufUtil;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_3222;

public abstract class AbstractPluginPacketSender_fabric implements IPluginPacketSender {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   public static final class_2960 WRAPPER_PACKET_RESOURCE = class_2960.method_60655("distant_horizons", "msg");
   private final boolean forgeByteInProtocolVersion;

   public AbstractPluginPacketSender_fabric() {
      this(false);
   }

   public AbstractPluginPacketSender_fabric(boolean forgeByteInProtocolVersion) {
      this.forgeByteInProtocolVersion = forgeByteInProtocolVersion;
   }

   @Override
   public final void sendToClient(IServerPlayerWrapper serverPlayer, AbstractNetworkMessage message) {
      this.sendToClient((class_3222)serverPlayer.getWrappedMcObject(), message);
   }

   public abstract void sendToClient(class_3222 arg, AbstractNetworkMessage abstractNetworkMessage);

   @Override
   public abstract void sendToServer(AbstractNetworkMessage abstractNetworkMessage);

   public AbstractNetworkMessage decodeMessage(class_2540 in) {
      AbstractNetworkMessage message = null;

      IncompatibleMessageInternalEvent var10;
      try {
         in.method_52932();
         int protocolVersion = this.forgeByteInProtocolVersion ? in.readByte() : in.readShort();
         if (protocolVersion == 15) {
            message = MessageRegistry.INSTANCE.createMessage(in.readUnsignedShort());
            message.decode(in);
            if (in.isReadable()) {
               throw new IOException("Buffer has not been fully read");
            }

            return message;
         }

         var10 = new IncompatibleMessageInternalEvent(protocolVersion);
      } catch (Exception var8) {
         in.method_52933();
         LOGGER.error("Failed to decode message", var8);
         LOGGER.error("Buffer: [" + in + "]");
         LOGGER.error("Buffer contents: [" + ByteBufUtil.hexDump(in) + "]");
         return new ProtocolErrorInternalEvent(var8, message, true);
      } finally {
         in.method_52988(in.writerIndex());
      }

      return var10;
   }

   public void encodeMessage(class_2540 out, AbstractNetworkMessage message) {
      Objects.requireNonNull(message);
      if (this.forgeByteInProtocolVersion) {
         out.method_52997(15);
      } else {
         out.method_52998(15);
      }

      try {
         out.method_52934();
         out.method_52998(MessageRegistry.INSTANCE.getMessageId(message));
         message.encode(out);
      } catch (Exception var4) {
         LOGGER.error("Failed to encode message", var4);
         LOGGER.error("Message: [" + message + "]");
         message.getSession().tryHandleMessage(new ProtocolErrorInternalEvent(var4, message, false));
         out.method_52935();
         AbstractNetworkMessage var5 = new CloseReasonMessage("Internal error on other side");
         out.method_52998(MessageRegistry.INSTANCE.getMessageId(var5));
         var5.encode(out);
      }
   }
}
