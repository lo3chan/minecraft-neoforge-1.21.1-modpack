package vazkii.psi.common.network.message;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.client.gui.GuiProgrammer;
import vazkii.psi.common.Psi;

public record MessageSpellError(String message, int x, int y) implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_spell_error");
   public static final Type<MessageSpellError> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpellError> CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      MessageSpellError::message,
      ByteBufCodecs.INT,
      MessageSpellError::x,
      ByteBufCodecs.INT,
      MessageSpellError::y,
      MessageSpellError::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(
         () -> {
            ChatComponent chatGui = Minecraft.getInstance().gui.getChat();
            Component chatMessage = Component.translatable(this.message, new Object[]{GuiProgrammer.convertIntToLetter(this.x), this.y})
               .setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            chatGui.addMessage(chatMessage);
         }
      );
   }
}
