package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.ClientboundPaintData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class PaintSystemSavedData extends SavedData {
   protected static final String DATA_NAME = "hexerei_book_paint_data";
   private Map<PaintSystemSavedData.BookPageIdentifier, PaintData> paintDataMap = new HashMap<>();
   public static final Codec<PaintSystemSavedData> CODEC = Codec.unboundedMap(PaintSystemSavedData.BookPageIdentifier.CODEC, PaintData.CODEC).xmap(map -> {
      PaintSystemSavedData data = new PaintSystemSavedData();
      data.paintDataMap.putAll(map);
      return data;
   }, data -> data.paintDataMap);

   private static PaintSystemSavedData create(CompoundTag tag, Provider registries) {
      PaintSystemSavedData data = new PaintSystemSavedData();
      data.load(tag, registries);
      return data;
   }

   public void load(CompoundTag tag, Provider registries) {
      this.paintDataMap.clear();
      if (tag.contains("paintDataMap")) {
         for (Tag value : tag.getList("paintDataMap", 10)) {
            PaintData pd = (PaintData)PaintData.CODEC.parse(NbtOps.INSTANCE, value).getOrThrow();
            this.paintDataMap.put(new PaintSystemSavedData.BookPageIdentifier(pd.page, pd.uuid), pd);
         }
      }
   }

   @NotNull
   public CompoundTag save(CompoundTag tag, Provider provider) {
      ListTag listTag = new ListTag();

      for (PaintData data : this.paintDataMap.values()) {
         listTag.add((Tag)PaintData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
      }

      tag.put("paintDataMap", listTag);
      return tag;
   }

   public void sendToClient(ServerPlayer player) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         for (PaintData data : this.paintDataMap.values()) {
            HexereiPacketHandler.sendToPlayerClient(new ClientboundPaintData(data), player);
         }
      }
   }

   public static void sendToClients() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         for (PaintData data : get().paintDataMap.values()) {
            HexereiPacketHandler.sendToAllPlayers(new ClientboundPaintData(data), server);
         }
      }
   }

   public PaintData getOrCreatePaintData(ResourceLocation bookLoc, UUID uuid) {
      PaintSystemSavedData.BookPageIdentifier bookPageIdentifier = new PaintSystemSavedData.BookPageIdentifier(bookLoc, uuid);
      if (!this.paintDataMap.containsKey(bookPageIdentifier)) {
         this.paintDataMap.put(bookPageIdentifier, new PaintData(32, 32, new ArrayList<>(), bookLoc, uuid));
      }

      return this.paintDataMap.get(bookPageIdentifier);
   }

   public PaintData getFirst() {
      return this.paintDataMap
         .values()
         .stream()
         .findFirst()
         .orElseGet(() -> new PaintData(16, 16, new ArrayList<>(), HexereiUtil.getResource("book_of_colors/page1"), new UUID(0L, 0L)));
   }

   public void putPaintData(PaintData data) {
      this.paintDataMap.put(new PaintSystemSavedData.BookPageIdentifier(data.page, data.uuid), data);
      this.setDirty();
   }

   public static Factory<PaintSystemSavedData> factory() {
      return new Factory(PaintSystemSavedData::new, PaintSystemSavedData::create, null);
   }

   public static PaintSystemSavedData get() {
      return (PaintSystemSavedData)ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(factory(), "hexerei_book_paint_data");
   }

   public record BookPageIdentifier(ResourceLocation bookLocation, UUID uuid) {
      public static final Codec<PaintSystemSavedData.BookPageIdentifier> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ResourceLocation.CODEC.fieldOf("page").forGetter(PaintSystemSavedData.BookPageIdentifier::bookLocation),
               UUIDUtil.CODEC.fieldOf("uuid").forGetter(PaintSystemSavedData.BookPageIdentifier::uuid)
            )
            .apply(instance, PaintSystemSavedData.BookPageIdentifier::new)
      );
      public static final StreamCodec<ByteBuf, PaintSystemSavedData.BookPageIdentifier> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
   }
}
