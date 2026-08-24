package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CofferInvUpdateWhitelistToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CofferInvUpdateWhitelistToServer> CODEC = StreamCodec.ofMember(
      CofferInvUpdateWhitelistToServer::encode, CofferInvUpdateWhitelistToServer::new
   );
   public static final Type<CofferInvUpdateWhitelistToServer> TYPE = new Type(HexereiUtil.getResource("coffer_inv_update_whitelist_slot"));
   int slotIndex;
   ItemStack stack;
   int slot;
   boolean remove;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CofferInvUpdateWhitelistToServer(int slotIndex, int slot, ItemStack stack) {
      this.slotIndex = slotIndex;
      this.stack = stack;
      this.slot = slot;
      this.remove = stack.isEmpty();
   }

   public CofferInvUpdateWhitelistToServer(RegistryFriendlyByteBuf buf) {
      this.slotIndex = buf.readInt();
      this.slot = buf.readInt();
      this.remove = buf.readBoolean();
      if (this.remove) {
         this.stack = ItemStack.EMPTY;
      } else {
         this.stack = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
      }
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.slotIndex);
      buffer.writeInt(this.slot);
      buffer.writeBoolean(this.remove);
      if (!this.remove) {
         ItemStack.STREAM_CODEC.encode(buffer, this.stack);
      }
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player != null) {
         if (Inventory.isHotbarSlot(this.slotIndex)) {
            if (this.slotIndex == player.getInventory().selected) {
               ItemStack handStack = player.getMainHandItem();
               if (handStack.getItem() instanceof CofferItem) {
                  NonNullList<ItemStack> whitelist = NonNullList.withSize(9, ItemStack.EMPTY);
                  CompoundTag tag = ((CustomData)handStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                  if (tag.contains("WhitelistItems", 9)) {
                     ListTag itemsTag = tag.getList("WhitelistItems", 10);

                     for (int i = 0; i < itemsTag.size(); i++) {
                        CompoundTag slotTag = itemsTag.getCompound(i);
                        int slot = slotTag.getInt("Slot");
                        if (slot >= 0 && slot < whitelist.size()) {
                           whitelist.set(slot, ItemStack.parse(Hexerei.DynamicRegistries.get(), slotTag.getCompound("Item")).orElse(ItemStack.EMPTY));
                        }
                     }
                  }

                  if (whitelist.stream().noneMatch(stack1 -> ItemStack.isSameItemSameComponents(this.stack, stack1)) || this.stack.isEmpty()) {
                     int writeIndex = 0;

                     for (int readIndex = 0; readIndex < whitelist.size(); readIndex++) {
                        if (!((ItemStack)whitelist.get(readIndex)).isEmpty()) {
                           if (readIndex != writeIndex) {
                              whitelist.set(writeIndex, ((ItemStack)whitelist.get(readIndex)).copy());
                              whitelist.set(readIndex, ItemStack.EMPTY);
                           }

                           writeIndex++;
                        }
                     }

                     whitelist.set(this.stack.isEmpty() ? this.slot : writeIndex, this.stack);
                     ListTag itemsTag = new ListTag();

                     for (int slot = 0; slot < whitelist.size(); slot++) {
                        ItemStack stack = (ItemStack)whitelist.get(slot);
                        if (!stack.isEmpty()) {
                           CompoundTag slotTag = new CompoundTag();
                           slotTag.putInt("Slot", slot);
                           Tag itemTag = stack.save(Hexerei.DynamicRegistries.get(), slotTag);
                           slotTag.put("Item", itemTag);
                           itemsTag.add(slotTag);
                        }
                     }

                     tag.put("WhitelistItems", itemsTag);
                     handStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                  }
               }
            }
         } else {
            ItemStack handStack = player.getOffhandItem();
            if (handStack.getItem() instanceof CofferItem) {
               NonNullList<ItemStack> whitelistx = NonNullList.withSize(9, ItemStack.EMPTY);
               CompoundTag tagx = ((CustomData)handStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               if (tagx.contains("WhitelistItems", 9)) {
                  ListTag itemsTag = tagx.getList("WhitelistItems", 10);

                  for (int ix = 0; ix < itemsTag.size(); ix++) {
                     CompoundTag slotTag = itemsTag.getCompound(ix);
                     int slotx = slotTag.getInt("Slot");
                     if (slotx >= 0 && slotx < whitelistx.size()) {
                        whitelistx.set(slotx, ItemStack.parse(Hexerei.DynamicRegistries.get(), slotTag.getCompound("Item")).orElse(ItemStack.EMPTY));
                     }
                  }
               }

               if (whitelistx.stream().noneMatch(stack1 -> ItemStack.isSameItemSameComponents(this.stack, stack1)) || this.stack.isEmpty()) {
                  int writeIndex = 0;

                  for (int readIndexx = 0; readIndexx < whitelistx.size(); readIndexx++) {
                     if (!((ItemStack)whitelistx.get(readIndexx)).isEmpty()) {
                        if (readIndexx != writeIndex) {
                           whitelistx.set(writeIndex, ((ItemStack)whitelistx.get(readIndexx)).copy());
                           whitelistx.set(readIndexx, ItemStack.EMPTY);
                        }

                        writeIndex++;
                     }
                  }

                  whitelistx.set(this.stack.isEmpty() ? this.slot : writeIndex, this.stack);
                  ListTag itemsTag = new ListTag();

                  for (int slotx = 0; slotx < whitelistx.size(); slotx++) {
                     ItemStack stack = (ItemStack)whitelistx.get(slotx);
                     if (!stack.isEmpty()) {
                        CompoundTag slotTag = new CompoundTag();
                        slotTag.putInt("Slot", slotx);
                        Tag itemTag = stack.save(Hexerei.DynamicRegistries.get(), slotTag);
                        slotTag.put("Item", itemTag);
                        itemsTag.add(slotTag);
                     }
                  }

                  tagx.put("WhitelistItems", itemsTag);
                  handStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tagx));
               }
            }
         }
      }
   }
}
