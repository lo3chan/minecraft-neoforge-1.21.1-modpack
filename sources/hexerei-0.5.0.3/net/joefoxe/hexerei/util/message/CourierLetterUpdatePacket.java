package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.custom.CourierLetterItem;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CourierLetterUpdatePacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CourierLetterUpdatePacket> CODEC = StreamCodec.ofMember(
      CourierLetterUpdatePacket::encode, CourierLetterUpdatePacket::new
   );
   public static final Type<CourierLetterUpdatePacket> TYPE = new Type(HexereiUtil.getResource("courier_letter_update"));
   final CompoundTag lines;
   int slotIndex;
   boolean sealed;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CourierLetterUpdatePacket(int slotIndex, CompoundTag lines, boolean sealed) {
      this.slotIndex = slotIndex;
      this.lines = lines;
      this.sealed = sealed;
   }

   public CourierLetterUpdatePacket(RegistryFriendlyByteBuf buf) {
      this.slotIndex = buf.readInt();
      this.lines = buf.readNbt();
      this.sealed = buf.readBoolean();
   }

   public static void encode(CourierLetterUpdatePacket object, FriendlyByteBuf buffer) {
      buffer.writeInt(object.slotIndex);
      buffer.writeNbt(object.lines);
      buffer.writeBoolean(object.sealed);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player != null) {
         if (Inventory.isHotbarSlot(this.slotIndex)) {
            if (this.slotIndex == player.getInventory().selected) {
               ItemStack stack = player.getMainHandItem();
               if (stack.getItem() instanceof CourierLetterItem courierLetterItem) {
                  CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).copyTag();
                  tag.put("Message", this.lines);
                  if (this.sealed) {
                     tag.putBoolean("Sealed", true);
                  }

                  BlockItem.setBlockEntityData(stack, (BlockEntityType)ModTileEntities.COURIER_LETTER_TILE.get(), tag);
               }
            }
         } else {
            ItemStack stack = player.getOffhandItem();
            if (stack.getItem() instanceof CourierLetterItem courierLetterItem) {
               CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).copyTag();
               tag.put("Message", this.lines);
               if (this.sealed) {
                  tag.putBoolean("Sealed", true);
               }

               BlockItem.setBlockEntityData(stack, (BlockEntityType)ModTileEntities.COURIER_LETTER_TILE.get(), tag);
            }
         }
      }
   }
}
