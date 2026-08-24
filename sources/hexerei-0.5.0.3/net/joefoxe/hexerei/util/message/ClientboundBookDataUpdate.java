package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookChapter;
import net.joefoxe.hexerei.data.books.BookEntries;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.joefoxe.hexerei.data.books.BookPageEntry;
import net.joefoxe.hexerei.data.books.BookWritableTextBox;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ClientboundBookDataUpdate extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBookDataUpdate> CODEC = StreamCodec.ofMember(
      ClientboundBookDataUpdate::encode, ClientboundBookDataUpdate::new
   );
   public static final Type<ClientboundBookDataUpdate> TYPE = new Type(HexereiUtil.getResource("book_data_update_client"));
   BlockPos bookAltar;
   BookData bookData;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public ClientboundBookDataUpdate(BookOfShadowsAltarTile bookAltar, BookData bookData) {
      this.bookAltar = bookAltar.getBlockPos();
      this.bookData = bookData;
   }

   public ClientboundBookDataUpdate(RegistryFriendlyByteBuf buf) {
      this.bookAltar = buf.readBlockPos();
      this.bookData = (BookData)BookData.STREAM_CODEC.decode(buf);
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeBlockPos(this.bookAltar);
      BookData.STREAM_CODEC.encode(buffer, this.bookData);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      if (player.level().getBlockEntity(this.bookAltar) instanceof BookOfShadowsAltarTile altar) {
         altar.currentBook = this.bookData;

         for (ResourceLocation book : BookManager.getBookLocations()) {
            BookEntries bookEntries = BookManager.getBookEntries(book);
            if (bookEntries != null) {
               for (BookChapter bookChapter : bookEntries.chapterList) {
                  for (BookPageEntry bookPageEntry : bookChapter.pages) {
                     if (this.bookData.pageTexts().containsKey(bookPageEntry.location)) {
                        BookPage page = BookManager.getBookPages(book, ResourceLocation.parse(bookPageEntry.location));
                        if (page != null) {
                           for (BookWritableTextBox bookWritableTextBox : page.writableTextBoxes) {
                              bookWritableTextBox.client.clearDisplayCache(this.bookData.getUUID());
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
