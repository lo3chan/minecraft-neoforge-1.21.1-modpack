package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;

public class BookSyncDataPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BookSyncDataPacket> CODEC = StreamCodec.ofMember(
      BookSyncDataPacket::encode, BookSyncDataPacket::new
   );
   public static final Type<BookSyncDataPacket> TYPE = new Type(HexereiUtil.getResource("book_sync_data"));
   BlockPos pos;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BookSyncDataPacket(BlockPos pos) {
      this.pos = pos;
   }

   public BookSyncDataPacket(RegistryFriendlyByteBuf buffer) {
      this(buffer.readBlockPos());
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (minecraft.level.getBlockEntity(this.pos) instanceof BookOfShadowsAltarTile book) {
         book.currentBook = (BookData)book.itemHandler.getStackInSlot(0).get(ModDataComponents.BOOK);
      }
   }
}
