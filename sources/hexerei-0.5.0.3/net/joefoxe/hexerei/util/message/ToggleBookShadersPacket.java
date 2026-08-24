package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.command.ToggleBookShadersCommand;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class ToggleBookShadersPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ToggleBookShadersPacket> CODEC = StreamCodec.ofMember(
      ToggleBookShadersPacket::encode, ToggleBookShadersPacket::new
   );
   public static final Type<ToggleBookShadersPacket> TYPE = new Type(HexereiUtil.getResource("toggle_book_shaders"));
   boolean enabled;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public ToggleBookShadersPacket(boolean enabled) {
      this.enabled = enabled;
   }

   public ToggleBookShadersPacket(RegistryFriendlyByteBuf buf) {
      this.enabled = buf.readBoolean();
   }

   public static void encode(ToggleBookShadersPacket object, RegistryFriendlyByteBuf buffer) {
      buffer.writeBoolean(object.enabled);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      ToggleBookShadersCommand.toggleConfig(this.enabled);
   }
}
