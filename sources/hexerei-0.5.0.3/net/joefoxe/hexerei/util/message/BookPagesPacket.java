package net.joefoxe.hexerei.util.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BookPagesPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, BookPagesPacket> CODEC = StreamCodec.ofMember(BookPagesPacket::encode, BookPagesPacket::new);
   public static final Type<BookPagesPacket> TYPE = new Type(HexereiUtil.getResource("book_pages"));
   protected final Map<ResourceLocation, Map<ResourceLocation, BookPage>> bookPages;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BookPagesPacket(Map<ResourceLocation, Map<ResourceLocation, BookPage>> bookPages) {
      this.bookPages = bookPages;
   }

   public BookPagesPacket(RegistryFriendlyByteBuf buf) {
      int size = buf.readInt();
      this.bookPages = new HashMap<>();

      for (int i = 0; i < size; i++) {
         ResourceLocation book = buf.readResourceLocation();
         int size2 = buf.readInt();

         for (int j = 0; j < size2; j++) {
            ResourceLocation name = buf.readResourceLocation();
            CompoundTag tag = buf.readNbt();
            if (tag != null) {
               BookPage bookPage = BookPage.loadFromTag(tag);
               if (!this.bookPages.containsKey(book)) {
                  this.bookPages.put(book, new HashMap<>());
               }

               this.bookPages.get(book).put(name, bookPage);
            }
         }
      }
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.bookPages.size());

      for (Entry<ResourceLocation, Map<ResourceLocation, BookPage>> entry : this.bookPages.entrySet()) {
         buffer.writeResourceLocation(entry.getKey());
         buffer.writeInt(entry.getValue().size());

         for (Entry<ResourceLocation, BookPage> entry2 : entry.getValue().entrySet()) {
            buffer.writeResourceLocation(entry2.getKey());
            buffer.writeNbt(BookPage.saveToTag(entry2.getValue()));
         }
      }
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      this.bookPages.keySet().forEach(book -> {
         BookManager.clearBookPages(book);
         this.bookPages.get(book).forEach((loc, map) -> {
            BookPage bookPage = this.bookPages.get(book).get(loc);
            bookPage.location = loc;
            BookManager.addBookPage(book, loc, bookPage);
         });
      });
   }
}
