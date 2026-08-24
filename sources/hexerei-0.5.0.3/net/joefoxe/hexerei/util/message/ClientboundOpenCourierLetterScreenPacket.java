package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.screen.CourierLetterScreen;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientboundOpenCourierLetterScreenPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenCourierLetterScreenPacket> CODEC = StreamCodec.ofMember(
      ClientboundOpenCourierLetterScreenPacket::encode, ClientboundOpenCourierLetterScreenPacket::new
   );
   public static final Type<ClientboundOpenCourierLetterScreenPacket> TYPE = new Type(HexereiUtil.getResource("open_courier_letter_screen"));
   int slotIndex;
   InteractionHand hand;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public ClientboundOpenCourierLetterScreenPacket(int slotIndex, InteractionHand hand) {
      this.slotIndex = slotIndex;
      this.hand = hand;
   }

   public ClientboundOpenCourierLetterScreenPacket(RegistryFriendlyByteBuf buf) {
      this(buf.readInt(), buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.slotIndex);
      buffer.writeBoolean(this.hand == InteractionHand.MAIN_HAND);
   }

   public static ClientboundOpenCourierLetterScreenPacket decode(FriendlyByteBuf buffer) {
      return new ClientboundOpenCourierLetterScreenPacket(buffer.readInt(), buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      minecraft.setScreen(
         new CourierLetterScreen(
            this.slotIndex, this.hand, this.slotIndex > 0 ? player.getInventory().getItem(this.slotIndex) : player.getItemInHand(InteractionHand.OFF_HAND)
         )
      );
   }
}
