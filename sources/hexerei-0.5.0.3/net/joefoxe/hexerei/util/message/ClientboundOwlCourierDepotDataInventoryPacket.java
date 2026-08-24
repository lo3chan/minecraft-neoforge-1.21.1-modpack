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

public class ClientboundOwlCourierDepotDataInventoryPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOwlCourierDepotDataInventoryPacket> CODEC = StreamCodec.ofMember(
      ClientboundOwlCourierDepotDataInventoryPacket::encode, ClientboundOwlCourierDepotDataInventoryPacket::new
   );
   public static final Type<ClientboundOwlCourierDepotDataInventoryPacket> TYPE = new Type(HexereiUtil.getResource("owl_courier_depot_inv_clientbound"));
   CompoundTag tag;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CompoundTag getTag() {
      return this.tag;
   }

   public ClientboundOwlCourierDepotDataInventoryPacket(CompoundTag tag) {
      this.tag = tag;
   }

   public ClientboundOwlCourierDepotDataInventoryPacket(RegistryFriendlyByteBuf buf) {
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
