package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BookBookmarkSwapToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BookBookmarkSwapToServer> CODEC = StreamCodec.ofMember(
      BookBookmarkSwapToServer::encode, BookBookmarkSwapToServer::new
   );
   public static final Type<BookBookmarkSwapToServer> TYPE = new Type(HexereiUtil.getResource("book_bookmark_swap_server"));
   BlockPos bookAltar;
   int slot;
   int slot2;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BookBookmarkSwapToServer(BookOfShadowsAltarTile bookAltar, int slot, int slot2) {
      this.bookAltar = bookAltar.getBlockPos();
      this.slot = slot;
      this.slot2 = slot2;
   }

   public BookBookmarkSwapToServer(RegistryFriendlyByteBuf buf) {
      this.bookAltar = buf.readBlockPos();
      this.slot = buf.readInt();
      this.slot2 = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.bookAltar);
      buffer.writeInt(this.slot);
      buffer.writeInt(this.slot2);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getBlockEntity(this.bookAltar) instanceof BookOfShadowsAltarTile book) {
         book.swapBookmarks(this.slot, this.slot2);
      }
   }
}
