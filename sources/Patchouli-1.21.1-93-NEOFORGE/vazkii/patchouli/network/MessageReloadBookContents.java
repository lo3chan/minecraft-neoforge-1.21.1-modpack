package vazkii.patchouli.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record MessageReloadBookContents() implements CustomPacketPayload {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("patchouli", "reload_books");
   public static final StreamCodec<FriendlyByteBuf, MessageReloadBookContents> CODEC = StreamCodec.unit(new MessageReloadBookContents());
   public static final Type<MessageReloadBookContents> TYPE = new Type(ID);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
