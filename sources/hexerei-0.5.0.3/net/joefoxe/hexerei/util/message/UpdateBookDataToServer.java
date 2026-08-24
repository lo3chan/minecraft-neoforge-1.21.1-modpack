package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class UpdateBookDataToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBookDataToServer> CODEC = StreamCodec.ofMember(
      UpdateBookDataToServer::encode, UpdateBookDataToServer::new
   );
   public static final Type<UpdateBookDataToServer> TYPE = new Type(HexereiUtil.getResource("update_book_data_server"));
   int type;
   InteractionHand hand;
   BlockPos pos;
   BookData bookData;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public UpdateBookDataToServer(InteractionHand hand, BookData bookData) {
      this.type = 0;
      this.hand = hand;
      this.bookData = bookData;
   }

   public UpdateBookDataToServer(BlockPos pos, BookData bookData) {
      this.type = 1;
      this.pos = pos;
      this.bookData = bookData;
   }

   public UpdateBookDataToServer(RegistryFriendlyByteBuf buffer) {
      this.type = buffer.readInt();
      if (this.type == 0) {
         this.hand = buffer.readInt() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
      } else {
         this.pos = buffer.readBlockPos();
      }

      this.bookData = (BookData)BookData.STREAM_CODEC.decode(buffer);
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.type);
      if (this.type == 0) {
         buffer.writeInt(this.hand.ordinal());
      } else {
         buffer.writeBlockPos(this.pos);
      }

      BookData.STREAM_CODEC.encode(buffer, this.bookData);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (this.type == 0) {
         ItemStack stack = player.getItemInHand(this.hand);
         if (stack.has(ModDataComponents.BOOK)) {
            stack.set(ModDataComponents.BOOK, this.bookData);
            player.setItemInHand(this.hand, stack);
         }
      } else if (player.level().getBlockEntity(this.pos) instanceof BookOfShadowsAltarTile altarTile) {
         ItemStack stack = altarTile.itemHandler.getStackInSlot(0);
         if (stack.has(ModDataComponents.BOOK)) {
            stack.set(ModDataComponents.BOOK, this.bookData);
            HexereiPacketHandler.sendToNearbyClient(player.level(), this.pos, new ClientboundBookDataUpdate(altarTile, this.bookData));
         }
      }
   }
}
