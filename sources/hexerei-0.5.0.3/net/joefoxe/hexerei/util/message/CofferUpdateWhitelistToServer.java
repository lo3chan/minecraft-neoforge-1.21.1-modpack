package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CofferUpdateWhitelistToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, CofferUpdateWhitelistToServer> CODEC = StreamCodec.ofMember(
      CofferUpdateWhitelistToServer::encode, CofferUpdateWhitelistToServer::new
   );
   public static final Type<CofferUpdateWhitelistToServer> TYPE = new Type(HexereiUtil.getResource("coffer_whitelist_update_slot"));
   BlockPos cofferTile;
   ItemStack stack;
   int slot;
   boolean remove;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public CofferUpdateWhitelistToServer(CofferTile cofferTile, int slot, ItemStack stack) {
      this.cofferTile = cofferTile.getBlockPos();
      this.stack = stack;
      this.slot = slot;
      this.remove = stack.isEmpty();
   }

   public CofferUpdateWhitelistToServer(RegistryFriendlyByteBuf buf) {
      this.cofferTile = buf.readBlockPos();
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
      buffer.writeBlockPos(this.cofferTile);
      buffer.writeInt(this.slot);
      buffer.writeBoolean(this.remove);
      if (!this.remove) {
         ItemStack.STREAM_CODEC.encode(buffer, this.stack);
      }
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getBlockEntity(this.cofferTile) instanceof CofferTile coffer
         && (coffer.whitelist.stream().noneMatch(stack1 -> ItemStack.isSameItemSameComponents(this.stack, stack1)) || this.stack.isEmpty())) {
         int writeIndex = 0;
         boolean empty = this.stack.isEmpty();
         if (empty) {
            coffer.whitelist.set(this.slot, this.stack);
         }

         for (int readIndex = 0; readIndex < coffer.whitelist.size(); readIndex++) {
            if (!((ItemStack)coffer.whitelist.get(readIndex)).isEmpty()) {
               if (readIndex != writeIndex) {
                  coffer.whitelist.set(writeIndex, ((ItemStack)coffer.whitelist.get(readIndex)).copy());
                  coffer.whitelist.set(readIndex, ItemStack.EMPTY);
               }

               writeIndex++;
            }
         }

         if (!empty) {
            coffer.whitelist.set(writeIndex, this.stack);
         }

         coffer.sync();
      }
   }
}
