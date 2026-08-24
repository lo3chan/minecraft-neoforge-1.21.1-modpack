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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public abstract class AbstractPluginPacketSender_neoforge implements IPluginPacketSender {
   private static final DhLogger LOGGER = new DhLoggerBuilder().fileLevelConfig(Config.Common.Logging.logNetworkEventToFile).build();
   public static final ResourceLocation WRAPPER_PACKET_RESOURCE = ResourceLocation.fromNamespaceAndPath("distant_horizons", "msg");
   private final boolean forgeByteInProtocolVersion;

   public AbstractPluginPacketSender_neoforge() {
      this(false);
   }

   public AbstractPluginPacketSender_neoforge(boolean forgeByteInProtocolVersion) {
      this.forgeByteInProtocolVersion = forgeByteInProtocolVersion;
   }

   @Override
   public final void sendToClient(IServerPlayerWrapper serverPlayer, AbstractNetworkMessage message) {
      this.sendToClient((ServerPlayer)serverPlayer.getWrappedMcObject(), message);
   }

   public abstract void sendToClient(ServerPlayer serverPlayer, AbstractNetworkMessage abstractNetworkMessage);

   @Override
   public abstract void sendToServer(AbstractNetworkMessage abstractNetworkMessage);

   public AbstractNetworkMessage decodeMessage(FriendlyByteBuf in) {
      AbstractNetworkMessage message = null;

      IncompatibleMessageInternalEvent var10;
      try {
         in.markReaderIndex();
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
         in.resetReaderIndex();
         LOGGER.error("Failed to decode message", var8);
         LOGGER.error("Buffer: [" + in + "]");
         LOGGER.error("Buffer contents: [" + ByteBufUtil.hexDump(in) + "]");
         return new ProtocolErrorInternalEvent(var8, message, true);
      } finally {
         in.readerIndex(in.writerIndex());
      }

      return var10;
   }

   public void encodeMessage(FriendlyByteBuf out, AbstractNetworkMessage message) {
      Objects.requireNonNull(message);
      if (this.forgeByteInProtocolVersion) {
         out.writeByte(15);
      } else {
         out.writeShort(15);
      }

      try {
         out.markWriterIndex();
         out.writeShort(MessageRegistry.INSTANCE.getMessageId(message));
         message.encode(out);
      } catch (Exception var4) {
         LOGGER.error("Failed to encode message", var4);
         LOGGER.error("Message: [" + message + "]");
         message.getSession().tryHandleMessage(new ProtocolErrorInternalEvent(var4, message, false));
         out.resetWriterIndex();
         AbstractNetworkMessage var5 = new CloseReasonMessage("Internal error on other side");
         out.writeShort(MessageRegistry.INSTANCE.getMessageId(var5));
         var5.encode(out);
      }
   }
}
