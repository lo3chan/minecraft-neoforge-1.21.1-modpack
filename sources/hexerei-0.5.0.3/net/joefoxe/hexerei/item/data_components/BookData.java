package net.joefoxe.hexerei.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public record BookData(
   UUID uuid, ResourceLocation book, int chapter, int page, boolean opened, BookData.Bookmarks bookmarks, Map<String, BookData.PageText> pageTexts
) {
   public static final UUID EMPTY_UUID = new UUID(0L, 0L);
   public static final BookData EMPTY = new BookData(
      EMPTY_UUID,
      HexereiUtil.getResource("book_of_shadows"),
      0,
      0,
      false,
      new BookData.Bookmarks(IntStream.range(0, 20).mapToObj(index -> new BookData.Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())),
      new HashMap<>()
   );
   public static final BookData EMPTY_NOTEBOOK = new BookData(
      EMPTY_UUID,
      HexereiUtil.getResource("notebook"),
      0,
      0,
      false,
      new BookData.Bookmarks(IntStream.range(0, 20).mapToObj(index -> new BookData.Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())),
      new HashMap<>()
   );
   public static final Function<ResourceLocation, BookData> EMPTY_AS = resourceLoc -> new BookData(
      EMPTY_UUID,
      resourceLoc,
      0,
      0,
      false,
      new BookData.Bookmarks(IntStream.range(0, 20).mapToObj(index -> new BookData.Bookmarks.Slot("", DyeColor.WHITE, index)).collect(Collectors.toList())),
      new HashMap<>()
   );
   private static final Codec<Map<String, String>> INNER_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING);
   public static final Codec<Map<String, Map<String, String>>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, INNER_MAP_CODEC);
   public static final Codec<BookData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("uuid", EMPTY_UUID).forGetter(BookData::uuid),
            ResourceLocation.CODEC.fieldOf("book").forGetter(BookData::getBook),
            Codec.INT.fieldOf("chapter").forGetter(BookData::getChapter),
            Codec.INT.fieldOf("page").forGetter(BookData::getPage),
            Codec.BOOL.fieldOf("opened").forGetter(BookData::isOpened),
            BookData.Bookmarks.CODEC.fieldOf("bookmarks").forGetter(BookData::getBookmarks),
            Codec.unboundedMap(Codec.STRING, BookData.PageText.CODEC).optionalFieldOf("pageTexts", new HashMap()).forGetter(BookData::pageTexts)
         )
         .apply(instance, BookData::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, BookData> STREAM_CODEC = StreamCodec.of(BookData::toNetwork, BookData::fromNetwork);

   private static BookData fromNetwork(RegistryFriendlyByteBuf buffer) {
      UUID uuid = (UUID)UUIDUtil.STREAM_CODEC.decode(buffer);
      ResourceLocation book = (ResourceLocation)ResourceLocation.STREAM_CODEC.decode(buffer);
      int chapter = (Integer)ByteBufCodecs.INT.decode(buffer);
      int page = (Integer)ByteBufCodecs.INT.decode(buffer);
      boolean opened = (Boolean)ByteBufCodecs.BOOL.decode(buffer);
      BookData.Bookmarks bookmarks = (BookData.Bookmarks)BookData.Bookmarks.STREAM_CODEC.decode(buffer);
      Map<String, BookData.PageText> pageTexts = new HashMap<>();
      int outerSize = buffer.readInt();

      for (int i = 0; i < outerSize; i++) {
         String outerKey = buffer.readUtf();
         int innerSize = buffer.readInt();
         Map<String, String> innerMap = new HashMap<>();

         for (int j = 0; j < innerSize; j++) {
            String innerKey = buffer.readUtf();
            String innerValue = buffer.readUtf();
            innerMap.put(innerKey, innerValue);
         }

         pageTexts.put(outerKey, new BookData.PageText(innerMap));
      }

      return new BookData(uuid, book, chapter, page, opened, bookmarks, pageTexts);
   }

   private static void toNetwork(RegistryFriendlyByteBuf buffer, BookData bookData) {
      UUIDUtil.STREAM_CODEC.encode(buffer, bookData.uuid);
      ResourceLocation.STREAM_CODEC.encode(buffer, bookData.book);
      ByteBufCodecs.INT.encode(buffer, bookData.chapter);
      ByteBufCodecs.INT.encode(buffer, bookData.page);
      ByteBufCodecs.BOOL.encode(buffer, bookData.opened);
      BookData.Bookmarks.STREAM_CODEC.encode(buffer, bookData.bookmarks);
      buffer.writeInt(bookData.pageTexts.size());
      bookData.pageTexts.forEach((s1, map) -> {
         buffer.writeUtf(s1);
         buffer.writeInt(map.pageTexts.size());
         map.pageTexts.forEach((ss, s) -> {
            buffer.writeUtf(ss);
            buffer.writeUtf(s);
         });
      });
   }

   public BookData updateTextBoxText(String pageId, int textBoxIndex, String text) {
      Map<String, BookData.PageText> newPageTexts = new HashMap<>(this.pageTexts);
      BookData.PageText textBoxTexts;
      if (newPageTexts.containsKey(pageId)) {
         textBoxTexts = new BookData.PageText(new HashMap<>(newPageTexts.get(pageId).pageTexts));
      } else {
         textBoxTexts = new BookData.PageText(new HashMap<>());
      }

      if (textBoxTexts.pageTexts == null) {
         textBoxTexts.pageTexts = new HashMap<>();
      }

      textBoxTexts.pageTexts.put(String.valueOf(textBoxIndex), text);
      newPageTexts.put(pageId, textBoxTexts);
      return new BookData(this.uuid, this.book, this.chapter, this.page, this.opened, this.bookmarks, newPageTexts);
   }

   public String getTextBoxText(String pageId, int textBoxIndex) {
      BookData.PageText textBoxTexts = this.pageTexts.get(pageId);
      return textBoxTexts != null && textBoxTexts.pageTexts.containsKey(String.valueOf(textBoxIndex))
         ? textBoxTexts.pageTexts.get(String.valueOf(textBoxIndex))
         : "";
   }

   public ResourceLocation getBook() {
      return this.book;
   }

   public int getChapter() {
      return this.chapter;
   }

   public int getPage() {
      return this.page;
   }

   public boolean isOpened() {
      return this.opened;
   }

   public BookData.Bookmarks getBookmarks() {
      return this.bookmarks;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public BookData setUUID(UUID uuid) {
      return new BookData(uuid, this.book, this.chapter, this.page, this.opened, this.bookmarks, this.pageTexts);
   }

   public BookData setBookmarks(BookData.Bookmarks bookmarks) {
      return new BookData(this.uuid, this.book, this.chapter, this.page, this.opened, bookmarks, this.pageTexts);
   }

   public BookData setChapter(int chapter) {
      return new BookData(this.uuid, this.book, chapter, this.page, this.opened, this.bookmarks, this.pageTexts);
   }

   public BookData setPage(int page) {
      return new BookData(this.uuid, this.book, this.chapter, page, this.opened, this.bookmarks, this.pageTexts);
   }

   public BookData setOpened(boolean opened) {
      return new BookData(this.uuid, this.book, this.chapter, this.page, opened, this.bookmarks, this.pageTexts);
   }

   public BookData setBook(ResourceLocation book) {
      return new BookData(this.uuid, book, this.chapter, this.page, this.opened, this.bookmarks, this.pageTexts);
   }

   public static class Bookmarks {
      List<BookData.Bookmarks.Slot> slots;
      public static final Codec<BookData.Bookmarks> CODEC = BookData.Bookmarks.Slot.CODEC.listOf().xmap(BookData.Bookmarks::new, contents -> contents.slots);
      public static final StreamCodec<ByteBuf, BookData.Bookmarks> STREAM_CODEC = BookData.Bookmarks.Slot.STREAM_CODEC
         .apply(ByteBufCodecs.list())
         .map(BookData.Bookmarks::new, contents -> contents.slots);

      public Bookmarks(List<BookData.Bookmarks.Slot> slots) {
         this.slots = slots;
      }

      public List<BookData.Bookmarks.Slot> getSlots() {
         return this.slots;
      }

      public BookData.Bookmarks.Slot getSlot(int index) {
         return this.slots.get(index);
      }

      public void setSlots(List<BookData.Bookmarks.Slot> slots) {
         this.slots = slots;
      }

      public void setSlot(int index, BookData.Bookmarks.Slot slot) {
         this.slots.set(index, slot);
      }

      public static class Slot {
         String id;
         DyeColor color;
         int index;
         public static final Codec<BookData.Bookmarks.Slot> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                  Codec.STRING.fieldOf("id").forGetter(BookData.Bookmarks.Slot::getId),
                  DyeColor.CODEC.fieldOf("color").forGetter(BookData.Bookmarks.Slot::getColor),
                  Codec.INT.fieldOf("index").forGetter(BookData.Bookmarks.Slot::getIndex)
               )
               .apply(instance, BookData.Bookmarks.Slot::new)
         );
         public static StreamCodec<ByteBuf, BookData.Bookmarks.Slot> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

         public Slot(String id, DyeColor color, int index) {
            this.id = id;
            this.color = color;
            this.index = index;
         }

         public String getId() {
            return this.id;
         }

         public DyeColor getColor() {
            return this.color;
         }

         public void setId(String id) {
            this.id = id;
         }

         public void setColor(DyeColor color) {
            this.color = color;
         }

         public int getIndex() {
            return this.index;
         }

         public void setIndex(int index) {
            this.index = index;
         }

         public BookData.Bookmarks.Slot copy() {
            return new BookData.Bookmarks.Slot(this.id, this.color, this.index);
         }

         public BookData.Bookmarks.Slot copyWithIndex(int index) {
            return new BookData.Bookmarks.Slot(this.id, this.color, index);
         }
      }
   }

   public static class PageText {
      public Map<String, String> pageTexts;
      public static final Codec<BookData.PageText> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("text", new HashMap()).forGetter(BookData.PageText::getPageTexts)
            )
            .apply(instance, BookData.PageText::new)
      );

      public Map<String, String> getPageTexts() {
         return this.pageTexts;
      }

      public PageText(Map<String, String> pageTexts) {
         this.pageTexts = pageTexts;
      }
   }
}
