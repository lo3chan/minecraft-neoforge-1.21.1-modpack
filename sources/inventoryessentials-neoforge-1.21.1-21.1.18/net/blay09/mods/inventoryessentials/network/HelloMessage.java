package net.blay09.mods.inventoryessentials.network;

import net.blay09.mods.inventoryessentials.InventoryEssentials;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class HelloMessage implements CustomPacketPayload {
   public static final Type<HelloMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "hello"));

   public static void encode(FriendlyByteBuf buf, HelloMessage message) {
   }

   public static HelloMessage decode(FriendlyByteBuf buf) {
      return new HelloMessage();
   }

   public static void handle(Player player, HelloMessage message) {
      InventoryEssentials.isServerSideInstalled = true;
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
