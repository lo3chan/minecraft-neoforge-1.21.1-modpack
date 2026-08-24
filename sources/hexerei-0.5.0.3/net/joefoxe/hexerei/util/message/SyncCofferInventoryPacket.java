package net.joefoxe.hexerei.util.message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.joefoxe.hexerei.data.coffer.ClientCofferData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SyncCofferInventoryPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncCofferInventoryPacket> CODEC = StreamCodec.ofMember(
      SyncCofferInventoryPacket::encode, SyncCofferInventoryPacket::new
   );
   public static final Type<SyncCofferInventoryPacket> TYPE = new Type(HexereiUtil.getResource("sync_coffer_inv_client"));
   private final UUID cofferId;
   private final Map<Integer, ItemStack> slotToItemMap;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public SyncCofferInventoryPacket(UUID cofferId, NonNullList<ItemStack> items) {
      this.cofferId = cofferId;
      this.slotToItemMap = new HashMap<>();

      for (int i = 0; i < items.size(); i++) {
         ItemStack stack = (ItemStack)items.get(i);
         if (!stack.isEmpty()) {
            this.slotToItemMap.put(i, stack);
         }
      }
   }

   public SyncCofferInventoryPacket(RegistryFriendlyByteBuf buf) {
      this.cofferId = buf.readUUID();
      this.slotToItemMap = new HashMap<>();
      int numNonEmptyStacks = buf.readVarInt();

      for (int i = 0; i < numNonEmptyStacks; i++) {
         int slot = buf.readVarInt();
         ItemStack stack = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
         this.slotToItemMap.put(slot, stack);
      }
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buf) {
      buf.writeUUID(this.cofferId);
      buf.writeVarInt(this.slotToItemMap.size());
      this.slotToItemMap.forEach((slot, stack) -> {
         buf.writeVarInt(slot);
         ItemStack.STREAM_CODEC.encode(buf, stack);
      });
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
      this.slotToItemMap.forEach(items::set);
      ClientCofferData.storeInventory(this.cofferId, items);
   }
}
