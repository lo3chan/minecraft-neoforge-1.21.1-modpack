package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CofferInvCycleWhitelistButtonToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CofferInvCycleWhitelistButtonToServer> CODEC = StreamCodec.ofMember(
      CofferInvCycleWhitelistButtonToServer::encode, CofferInvCycleWhitelistButtonToServer::new
   );
   public static final Type<CofferInvCycleWhitelistButtonToServer> TYPE = new Type(HexereiUtil.getResource("coffer_inv_cycle_whitelist_button"));
   int slotIndex;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CofferInvCycleWhitelistButtonToServer(int slotIndex) {
      this.slotIndex = slotIndex;
   }

   public CofferInvCycleWhitelistButtonToServer(RegistryFriendlyByteBuf buf) {
      this.slotIndex = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.slotIndex);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player != null) {
         if (Inventory.isHotbarSlot(this.slotIndex)) {
            if (this.slotIndex == player.getInventory().selected) {
               ItemStack handStack = player.getMainHandItem();
               if (handStack.getItem() instanceof CofferItem) {
                  CompoundTag tag = ((CustomData)handStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                  CofferTile.WhitelistMode mode = CofferTile.WhitelistMode.WHITELIST_INV;
                  if (tag.contains("WhitelistMode")) {
                     mode = CofferTile.WhitelistMode.byId(tag.getInt("WhitelistMode"));
                  }

                  mode = CofferTile.WhitelistMode.byId((mode.ordinal() + 1) % CofferTile.WhitelistMode.values().length);
                  if (mode == CofferTile.WhitelistMode.WHITELIST_INV) {
                     tag.remove("WhitelistMode");
                  } else {
                     tag.putInt("WhitelistMode", mode.ordinal());
                  }

                  handStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
               }
            }
         } else {
            ItemStack handStack = player.getOffhandItem();
            if (handStack.getItem() instanceof CofferItem) {
               CompoundTag tagx = ((CustomData)handStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               CofferTile.WhitelistMode modex = CofferTile.WhitelistMode.WHITELIST_INV;
               if (tagx.contains("WhitelistMode")) {
                  modex = CofferTile.WhitelistMode.byId(tagx.getInt("WhitelistMode"));
               }

               modex = CofferTile.WhitelistMode.byId((modex.ordinal() + 1) % CofferTile.WhitelistMode.values().length);
               if (modex == CofferTile.WhitelistMode.WHITELIST_INV) {
                  tagx.remove("WhitelistMode");
               } else {
                  tagx.putInt("WhitelistMode", modex.ordinal());
               }

               handStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tagx));
            }
         }
      }
   }
}
