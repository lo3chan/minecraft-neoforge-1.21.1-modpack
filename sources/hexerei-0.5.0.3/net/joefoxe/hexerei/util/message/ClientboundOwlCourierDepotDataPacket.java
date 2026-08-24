package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class ClientboundOwlCourierDepotDataPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOwlCourierDepotDataPacket> CODEC = StreamCodec.ofMember(
      ClientboundOwlCourierDepotDataPacket::encode, ClientboundOwlCourierDepotDataPacket::new
   );
   public static final Type<ClientboundOwlCourierDepotDataPacket> TYPE = new Type(HexereiUtil.getResource("owl_courier_depot_clientbound"));
   CompoundTag tag;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CompoundTag getTag() {
      return this.tag;
   }

   public ClientboundOwlCourierDepotDataPacket(CompoundTag tag) {
      this.tag = tag;
   }

   public ClientboundOwlCourierDepotDataPacket(RegistryFriendlyByteBuf buf) {
      this.tag = buf.readNbt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeNbt(this.tag);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      ClientOwlCourierDepotData.update(this);
   }
}
