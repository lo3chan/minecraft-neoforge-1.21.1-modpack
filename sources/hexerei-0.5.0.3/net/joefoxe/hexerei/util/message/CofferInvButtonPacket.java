package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CofferInvButtonPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CofferInvButtonPacket> CODEC = StreamCodec.ofMember(
      CofferInvButtonPacket::encode, CofferInvButtonPacket::new
   );
   public static final Type<CofferInvButtonPacket> TYPE = new Type(HexereiUtil.getResource("coffer_inv_button"));
   int slotIndex;
   int buttonToggled;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CofferInvButtonPacket(int slotIndex, int buttonToggled) {
      this.slotIndex = slotIndex;
      this.buttonToggled = buttonToggled;
   }

   public CofferInvButtonPacket(RegistryFriendlyByteBuf buf) {
      this.slotIndex = buf.readInt();
      this.buttonToggled = buf.readInt();
   }

   public static void encode(CofferInvButtonPacket object, FriendlyByteBuf buffer) {
      buffer.writeInt(object.slotIndex);
      buffer.writeInt(object.buttonToggled);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player != null) {
         if (Inventory.isHotbarSlot(this.slotIndex)) {
            if (this.slotIndex == player.getInventory().selected) {
               ItemStack stack = player.getMainHandItem();
               if (stack.getItem() instanceof CofferItem) {
                  CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                  tag.putInt("ButtonToggled", this.buttonToggled);
                  stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
               }
            }
         } else {
            ItemStack stack = player.getOffhandItem();
            if (stack.getItem() instanceof CofferItem) {
               CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               tag.putInt("ButtonToggled", this.buttonToggled);
               stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
         }
      }
   }
}
