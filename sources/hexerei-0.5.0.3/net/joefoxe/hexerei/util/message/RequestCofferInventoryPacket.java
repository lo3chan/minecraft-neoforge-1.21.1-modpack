package net.joefoxe.hexerei.util.message;

import java.util.UUID;
import net.joefoxe.hexerei.data.coffer.CofferInventorySavedData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RequestCofferInventoryPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, RequestCofferInventoryPacket> CODEC = StreamCodec.ofMember(
      RequestCofferInventoryPacket::encode, RequestCofferInventoryPacket::new
   );
   public static final Type<RequestCofferInventoryPacket> TYPE = new Type(HexereiUtil.getResource("request_coffer_inv"));
   private final UUID cofferId;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buf) {
      buf.writeUUID(this.cofferId);
   }

   public RequestCofferInventoryPacket(UUID cofferId) {
      this.cofferId = cofferId;
   }

   public RequestCofferInventoryPacket(RegistryFriendlyByteBuf buf) {
      this.cofferId = buf.readUUID();
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      NonNullList<ItemStack> inventory = CofferInventorySavedData.get(server.overworld()).getInventory(this.cofferId);
      HexereiPacketHandler.sendToPlayerClient(new SyncCofferInventoryPacket(this.cofferId, inventory), player);
   }
}
