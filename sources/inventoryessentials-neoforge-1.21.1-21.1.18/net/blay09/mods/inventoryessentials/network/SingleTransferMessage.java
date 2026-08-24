package net.blay09.mods.inventoryessentials.network;

import net.blay09.mods.inventoryessentials.ServerInventoryTransfers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class SingleTransferMessage implements CustomPacketPayload {
   public static Type<SingleTransferMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "single_transfer"));
   private final int slotNumber;

   public SingleTransferMessage(int slotNumber) {
      this.slotNumber = slotNumber;
   }

   public static SingleTransferMessage decode(FriendlyByteBuf buf) {
      int slotNumber = buf.readVarInt();
      return new SingleTransferMessage(slotNumber);
   }

   public static void encode(FriendlyByteBuf buf, SingleTransferMessage message) {
      buf.writeVarInt(message.slotNumber);
   }

   public static void handle(ServerPlayer player, SingleTransferMessage message) {
      AbstractContainerMenu menu = player.containerMenu;
      if (menu != null && menu.isValidSlotIndex(message.slotNumber)) {
         Slot slot = menu.getSlot(message.slotNumber);
         ServerInventoryTransfers.singleTransfer(player, menu, slot);
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
