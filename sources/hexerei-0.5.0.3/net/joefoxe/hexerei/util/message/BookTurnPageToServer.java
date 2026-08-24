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

public class BookTurnPageToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BookTurnPageToServer> CODEC = StreamCodec.ofMember(
      BookTurnPageToServer::encode, BookTurnPageToServer::new
   );
   public static final Type<BookTurnPageToServer> TYPE = new Type(HexereiUtil.getResource("book_turn_page_server"));
   BlockPos bookAltar;
   int turnPage;
   int chapter;
   int page;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BookTurnPageToServer(BookOfShadowsAltarTile bookAltar, int turnPage, int chapter, int page) {
      this.bookAltar = bookAltar.getBlockPos();
      this.turnPage = turnPage;
      this.chapter = chapter;
      this.page = page;
   }

   public BookTurnPageToServer(RegistryFriendlyByteBuf buf) {
      this.bookAltar = buf.readBlockPos();
      this.turnPage = buf.readInt();
      this.chapter = buf.readInt();
      this.page = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.bookAltar);
      buffer.writeInt(this.turnPage);
      buffer.writeInt(this.chapter);
      buffer.writeInt(this.page);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getBlockEntity(this.bookAltar) instanceof BookOfShadowsAltarTile book) {
         book.setTurnPage(this.turnPage, this.chapter, this.page);
      }
   }
}
