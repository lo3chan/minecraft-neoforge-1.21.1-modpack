package vazkii.psi.common.network.message;

import java.nio.ByteBuffer;
import java.util.ListIterator;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;

public class MessageSpamlessChat implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_spamless_chat");
   public static final Type<MessageSpamlessChat> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpamlessChat> CODEC = new StreamCodec<RegistryFriendlyByteBuf, MessageSpamlessChat>() {
      @NotNull
      public MessageSpamlessChat decode(@NotNull RegistryFriendlyByteBuf pBuffer) {
         return new MessageSpamlessChat((Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(pBuffer), pBuffer.readInt());
      }

      public void encode(@NotNull RegistryFriendlyByteBuf pBuffer, MessageSpamlessChat message) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(pBuffer, message.message);
         pBuffer.writeInt(message.magic);
      }
   };
   private static final int BASE_MAGIC = 696969;
   private final Component message;
   private final MessageSignature signature;
   private final int magic;

   public MessageSpamlessChat(Component message, int magic) {
      this.message = message;
      this.magic = 696969 + magic;
      this.signature = new MessageSignature(ByteBuffer.allocate(256).putInt(this.magic).array());
   }

   public static void deleteMessage(ChatComponent chatGui, MessageSignature pMessageSignature) {
      ListIterator<GuiMessage> listiterator = chatGui.allMessages.listIterator();

      while (listiterator.hasNext()) {
         GuiMessage guimessage = listiterator.next();
         if (pMessageSignature.equals(guimessage.signature())) {
            listiterator.remove();
            break;
         }
      }

      chatGui.refreshTrimmedMessages();
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         ChatComponent chatGui = Minecraft.getInstance().gui.getChat();
         deleteMessage(chatGui, this.signature);
         chatGui.addMessage(this.message, this.signature, GuiMessageTag.system());
      });
   }
}
