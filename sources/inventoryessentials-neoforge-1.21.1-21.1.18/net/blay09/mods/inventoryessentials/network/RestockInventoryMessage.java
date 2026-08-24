package net.blay09.mods.inventoryessentials.network;

import net.blay09.mods.inventoryessentials.InventoryOperations;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class RestockInventoryMessage implements CustomPacketPayload {
   public static final RestockInventoryMessage INSTANCE = new RestockInventoryMessage();
   public static final Type<RestockInventoryMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "restock_inventory"));
   public static final StreamCodec<RegistryFriendlyByteBuf, RestockInventoryMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private RestockInventoryMessage() {
   }

   public static void handle(ServerPlayer player, RestockInventoryMessage message) {
      InventoryOperations.forServerPlayer(player).transferToInventory(player.containerMenu, player, true, false);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
