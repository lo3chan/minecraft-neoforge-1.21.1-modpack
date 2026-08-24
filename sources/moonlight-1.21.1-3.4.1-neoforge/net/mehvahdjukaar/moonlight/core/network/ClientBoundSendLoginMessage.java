package net.mehvahdjukaar.moonlight.core.network;

import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.misc.AntiRepostWarning;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.TypeAndCodec;

public class ClientBoundSendLoginMessage implements Message {
   public static final TypeAndCodec<RegistryFriendlyByteBuf, ClientBoundSendLoginMessage> TYPE = Message.makeType(
      Moonlight.res("s2c_send_login"), ClientBoundSendLoginMessage::new
   );

   public ClientBoundSendLoginMessage(RegistryFriendlyByteBuf buf) {
   }

   public ClientBoundSendLoginMessage() {
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
   }

   @Override
   public void handle(Message.Context context) {
      AntiRepostWarning.run();
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE.type();
   }
}
