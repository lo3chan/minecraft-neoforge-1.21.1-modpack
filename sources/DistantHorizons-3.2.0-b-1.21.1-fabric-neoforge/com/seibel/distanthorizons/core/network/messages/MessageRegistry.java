package com.seibel.distanthorizons.core.network.messages;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.seibel.distanthorizons.core.network.messages.base.CloseReasonMessage;
import com.seibel.distanthorizons.core.network.messages.base.CodecCrashMessage;
import com.seibel.distanthorizons.core.network.messages.base.LevelInitMessage;
import com.seibel.distanthorizons.core.network.messages.base.RequestLevelInitMessage;
import com.seibel.distanthorizons.core.network.messages.base.SessionConfigMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataPartialUpdateMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceRequestMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSourceResponseMessage;
import com.seibel.distanthorizons.core.network.messages.fullData.FullDataSplitMessage;
import com.seibel.distanthorizons.core.network.messages.requests.CancelMessage;
import com.seibel.distanthorizons.core.network.messages.requests.ExceptionMessage;
import com.seibel.distanthorizons.coreapi.ModInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MessageRegistry {
   public static final boolean DEBUG_CODEC_CRASH_MESSAGE = ModInfo.IS_DEV_BUILD;
   public static final MessageRegistry INSTANCE = new MessageRegistry();
   private final Map<Integer, Supplier<? extends AbstractNetworkMessage>> messageConstructorById = new HashMap<>();
   private final BiMap<Class<? extends AbstractNetworkMessage>, Integer> messageClassById = HashBiMap.create();

   private MessageRegistry() {
      this.registerMessage(CloseReasonMessage.class, CloseReasonMessage::new);
      this.registerMessage(LevelInitMessage.class, LevelInitMessage::new);
      this.registerMessage(RequestLevelInitMessage.class, RequestLevelInitMessage::new);
      this.registerMessage(SessionConfigMessage.class, SessionConfigMessage::new);
      this.registerMessage(CancelMessage.class, CancelMessage::new);
      this.registerMessage(ExceptionMessage.class, ExceptionMessage::new);
      this.registerMessage(FullDataSourceRequestMessage.class, FullDataSourceRequestMessage::new);
      this.registerMessage(FullDataSourceResponseMessage.class, FullDataSourceResponseMessage::new);
      this.registerMessage(FullDataPartialUpdateMessage.class, FullDataPartialUpdateMessage::new);
      this.registerMessage(FullDataSplitMessage.class, FullDataSplitMessage::new);
      if (DEBUG_CODEC_CRASH_MESSAGE) {
         this.registerMessage(CodecCrashMessage.class, CodecCrashMessage::new);
      }
   }

   public <T extends AbstractNetworkMessage> void registerMessage(Class<T> clazz, Supplier<T> supplier) {
      int id = this.messageConstructorById.size() + 1;
      this.messageConstructorById.put(id, supplier);
      this.messageClassById.put(clazz, id);
   }

   public AbstractNetworkMessage createMessage(int messageId) throws IllegalArgumentException {
      try {
         return this.messageConstructorById.get(messageId).get();
      } catch (NullPointerException var3) {
         throw new IllegalArgumentException("Invalid message ID: " + messageId);
      }
   }

   public int getMessageId(AbstractNetworkMessage message) {
      return this.getMessageId((Class<? extends AbstractNetworkMessage>)message.getClass());
   }

   public int getMessageId(Class<? extends AbstractNetworkMessage> messageClass) {
      try {
         return (Integer)this.messageClassById.get(messageClass);
      } catch (NullPointerException var3) {
         throw new IllegalArgumentException("Message does not have ID assigned to it: [" + messageClass.getSimpleName() + "].");
      }
   }
}
