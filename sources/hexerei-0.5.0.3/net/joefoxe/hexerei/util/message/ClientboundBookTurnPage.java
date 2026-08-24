package net.joefoxe.hexerei.util.message;

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

public class ClientboundBookTurnPage extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBookTurnPage> CODEC = StreamCodec.ofMember(
      ClientboundBookTurnPage::encode, ClientboundBookTurnPage::new
   );
   public static final Type<ClientboundBookTurnPage> TYPE = new Type(HexereiUtil.getResource("book_turn_page_client"));
   BlockPos bookAltar;
   int turnPage;
   int turnToChapter;
   int turnToPage;
   int chapter;
   int page;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public ClientboundBookTurnPage(BookOfShadowsAltarTile bookAltar, int turnPage, int turnToChapter, int turnToPage, int chapter, int page) {
      this.bookAltar = bookAltar.getBlockPos();
      this.turnPage = turnPage;
      this.turnToChapter = turnToChapter;
      this.turnToPage = turnToPage;
      this.chapter = chapter;
      this.page = page;
   }

   public ClientboundBookTurnPage(RegistryFriendlyByteBuf buf) {
      this.bookAltar = buf.readBlockPos();
      this.turnPage = buf.readInt();
      this.turnToChapter = buf.readInt();
      this.turnToPage = buf.readInt();
      this.chapter = buf.readInt();
      this.page = buf.readInt();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.bookAltar);
      buffer.writeInt(this.turnPage);
      buffer.writeInt(this.turnToChapter);
      buffer.writeInt(this.turnToPage);
      buffer.writeInt(this.chapter);
      buffer.writeInt(this.page);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getBlockEntity(this.bookAltar) instanceof BookOfShadowsAltarTile book) {
         if (this.turnPage == -2) {
            this.turnPage += 2;
         }

         book.turnPage = this.turnPage;
         book.turnToChapter = this.turnToChapter;
         book.turnToPage = this.turnToPage;
         book.currentBook = book.currentBook.setChapter(this.chapter);
         book.currentBook = book.currentBook.setPage(this.page);
         book.pageOneRotationRender = 0.0F;
         book.pageOneRotation = 0.0F;
         book.pageOneRotationTo = 0.0F;
         book.pageOneRotationLast = book.pageOneRotation;
         book.pageTwoRotationRender = 0.0F;
         book.pageTwoRotation = 0.0F;
         book.pageTwoRotationTo = 0.0F;
         book.pageTwoRotationLast = book.pageTwoRotation;
      }
   }
}
